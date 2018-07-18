/*
Triggers and functions for auxiliar properties implementation
*/

/*
It returns the species of the atoms in this material. It is derived from “Chemical formula”.
Example: Chemical formula =Fe3Sn1 -> Atomic species=Fe,Sn

The procedure concatenates the symbols of the formula in the composition table separated by comas
The procedure argument is the chemical formula
*/
\connect novamag

CREATE OR REPLACE FUNCTION f_atomic_species(text) RETURNS text AS $$

declare 
	result text;
begin
	select array_to_string(array_agg(symbol),',') atomic_species
	into result
	from composition 
	where formula=$1;
	
	return result;
end;

$$ LANGUAGE plpgsql;

/*
It returns the number of species in the system (e.g., binary = 2, ternary = 3, etc.). It is derived from “Chemical formula”.
Example: Chemical formula =Fe3Sn1 --> Species count=2

The procedure computes a select-count of the symbols of the formula in the composition table
The procedure argument is the chemical formula
*/
CREATE OR REPLACE FUNCTION f_species_count(text) RETURNS int AS $$
declare 
	result int;
begin
	select count(symbol)
	into result
	from composition
	where formula=$1;
	
	return result;
end;

$$ LANGUAGE plpgsql;

/*
Ir returns the lattice system, which is derived from “Space group”
	Lattice System	Space group
	TRI				1,2
	MON				3-15
	ORT				16-74
	TET				75-142
	RHO				146,148,155,160,161,166,167
	HEX				143-145,147,149-154,156-159, 162-165,168-194
	CUB				195-230

	The procedure argument is intended to be the space group
*/
CREATE OR REPLACE FUNCTION f_lattice_system(int) RETURNS text AS $$
declare 
	result text;
begin
	select  case 
			when $1 between 1 and 2 then 'TRIC'
			when $1 between 3 and 15 then 'MON'
			when $1 between 16 and 74 then 'ORT'
			when $1 between 75 and 142 then 'TET'
			when $1 in(146,148,155,160,161,166,167) then 'RHO'
			when $1 between 143 and 145
				or $1=147
				or $1 between 149 and 154
				or $1 between 156 and 159
				or $1 between 162 and 165
				or $1 between 168 and 194 then 'HEX'
			when $1 between 195 and 230 then 'CUB'
				
		end
	into result;
	
	if (result is null) then	
		RAISE EXCEPTION 'Nonexistent compound space group in item --> %', $1 USING ERRCODE = '23505';-------------------------->PROBAR
	end if;

	return result;
end;

$$ LANGUAGE plpgsql;

/*
It returns the unit cell atom count, which is the number of atoms in the unit cell (integer number).
It is derived from “Chemical formula”, just summing the number of atoms.
Example: Chemical formula =Fe3Sn1 -> Unit cell atom count=4

The procedure argument is the chemical formula

The inner subselect get the formula sub-indexes as text
For example:
select regexp_replace(unnest((regexp_split_to_array('Fe3Sn1', '[A-Z]'))[2:]), '[a-z]','') numb;
returns

numb
------
'3'
'1'

Then the outer select cast them to integers (if no sub-index then 1 is assiged)

*/
CREATE OR REPLACE FUNCTION f_unit_cell_atom_count(text) RETURNS int AS $$
declare 
	result int;
begin
	select sum (case numb 
			when '' then 1
			else cast(numb as integer)
		end)		
	from (select regexp_replace(unnest((regexp_split_to_array($1, '[A-Z]'))[2:]), '[a-z]','') numb) as t
	into result;	
	
	return result;
end;

$$ LANGUAGE plpgsql;

/*
Trigger body of the trigger t_auxiliar_props
It calls the functions above to update the fields
atomic_species, species_count, lattice_system and unit_cell_atom_count
of the items table

It also updates in the items table other fields computed as ratios:
atom_volume: Atom volume = “Unit cell volume”/ ”Unit cell atom count”.
atomic_energy: Atomic energy = Unit cell energy”/ “Unit cell atom count
atomic_formation_enthalpy: Atomic formation enthalpy = “Unit cell formation enthalpy”/“Unit cell atom count”
*/
create or replace function f_auxiliar_props() returns trigger as $$
	begin
		
		new.atomic_species := f_atomic_species(new.formula);
		new.species_count  := f_species_count(new.formula);
		--RAISE NOTICE 'formula= %', new.formula; 
		--RAISE NOTICE 'atomic_species= %', new.atomic_species; 
		

		new.lattice_system := f_lattice_system(new.compound_space_group);
		--RAISE NOTICE 'lattice system=%', new.lattice_system;
		new.unit_cell_atom_count := f_unit_cell_atom_count(new.formula);
		new.atom_volume := new.unit_cell_volume/new.unit_cell_atom_count;

		new.atomic_energy := new.unit_cell_energy/new.unit_cell_atom_count;
		new.atomic_formation_enthalpy := new.unit_cell_formation_enthalpy/new.unit_cell_atom_count;
		
		return new;
	end;
$$
LANGUAGE plpgsql;

drop trigger if exists t_auxiliar_props on items;

/*
Trigger that calls the functions above to update the fields
atomic_species, species_count, lattice_system and unit_cell_atom_count
of the items table

It also updates in the items table other fields computed as ratios:
atom_volume: Atom volume = “Unit cell volume”/ ”Unit cell atom count”.
atomic_energy: Atomic energy = Unit cell energy”/ “Unit cell atom count
atomic_formation_enthalpy: Atomic formation enthalpy = “Unit cell formation enthalpy”/“Unit cell atom count”
*/
create trigger t_auxiliar_props
before insert or update of formula, compound_space_group, unit_cell_volume, unit_cell_energy, unit_cell_formation_enthalpy on items
for each row
execute procedure f_auxiliar_props();
---------------------------------------------------------------------------------------------
/*
Trigger body of t_no_change_auxiliar_props
It raises an exception if someone tries to change the field Atomic_species manually
*/
create or replace function f_no_change_atomic_species() returns trigger as $$
	begin
		RAISE EXCEPTION 'Atomic_species is a read-only attribute. So, cannot be updated';
	end;
$$
LANGUAGE plpgsql;

drop trigger if exists t_no_change_auxiliar_props on items;

/*
Trigger that raises an exception if someone tries to change the field Atomic_species manually
*/
create trigger t_no_change_auxiliar_props
before update of atomic_species on items for statement
execute procedure f_no_change_atomic_species();
---------------------------------------------------------------------------------------------
/*
Trigger body of t_no_change_species_count
It raises an exception if someone tries to change the field species_count manually
*/
create or replace function f_no_change_species_count() returns trigger as $$
	begin
		RAISE EXCEPTION 'Species_count is a read-only attribute. So, cannot be updated';
	end;
$$
LANGUAGE plpgsql;
	
drop trigger if exists t_no_change_species_count on items;
/*
Trigger that raises an exception if someone tries to change the field species_count manually
*/
create trigger t_no_change_species_count
before update of species_count on items for statement
execute procedure f_no_change_species_count();
---------------------------------------------------------------------------------------------
/*
Trigger body of t_no_change_lattice_system
It raises an exception if someone tries to change the field lattice_system manually
*/
create or replace function f_no_change_lattice_system() returns trigger as $$
	begin
		RAISE EXCEPTION 'Lattice_system is a read-only attribute. So, cannot be updated';
	end;
$$
LANGUAGE plpgsql;
	
drop trigger if exists t_no_change_lattice_system on items;
/*
Trigger that raises an exception if someone tries to change the field lattice_system manually
*/
create trigger t_no_change_lattice_system
before update of lattice_system on items for statement
execute procedure f_no_change_lattice_system();
---------------------------------------------------------------------------------------------
/*
Trigger body of t_no_change_unit_cell_atom_count
It raises an exception if someone tries to change the field unit_cell_atom_count manually
*/
create or replace function f_no_change_unit_cell_atom_count() returns trigger as $$
	begin
		RAISE EXCEPTION 'Unit_cell_atom_count is a read-only attribute. So, cannot be updated';
	end;
$$
LANGUAGE plpgsql;
	
drop trigger if exists t_no_change_unit_cell_atom_count on items;
/*
Trigger that raises an exception if someone tries to change the field unit_cell_atom_count manually
*/
create trigger t_no_change_unit_cell_atom_count
before update of unit_cell_atom_count on items for statement
execute procedure f_no_change_unit_cell_atom_count();
---------------------------------------------------------------------------------------------
/*
Trigger body of t_no_change_atom_volume
It raises an exception if someone tries to change the field atom_volume manually
*/
create or replace function f_no_change_atom_volume() returns trigger as $$
	begin
		RAISE EXCEPTION 'Atom_volume is a read-only attribute. So, cannot be updated';
	end;
$$
LANGUAGE plpgsql;
	
drop trigger if exists t_no_change_atom_volume on items;
/*
Trigger that raises an exception if someone tries to change the field atom_volume manually
*/
create trigger t_no_change_atom_volume
before update of atom_volume on items for statement
execute procedure f_no_change_atom_volume();
---------------------------------------------------------------------------------------------
/*
Trigger body of t_no_change_atomic_energy
It raises an exception if someone tries to change the field atomic_energy manually
*/
create or replace function f_no_change_atomic_energy() returns trigger as $$
	begin
		RAISE EXCEPTION 'Atomic_energy is a read-only attribute. So, cannot be updated';
	end;
$$
LANGUAGE plpgsql;
	
drop trigger if exists t_no_change_atomic_energy on items;
/*
Trigger that raises an exception if someone tries to change the field atomic_energy manually
*/
create trigger t_no_change_atomic_energy
before update of atomic_energy on items for statement
execute procedure f_no_change_atomic_energy();
---------------------------------------------------------------------------------------------
/*
Trigger body of t_no_change_atomic_formation_enthalpy
It raises an exception if someone tries to change the field atomic_formation_enthalpy manually
*/
create or replace function f_no_change_atomic_formation_enthalpy() returns trigger as $$
	begin
		RAISE EXCEPTION 'Atomic_formation_enthalpy is a read-only attribute. So, cannot be updated';
	end;
$$
LANGUAGE plpgsql;
	
drop trigger if exists t_no_change_atomic_formation_enthalpy on items;
/*
Trigger that raises an exception if someone tries to change the field atomic_formation_enthalpy manually
*/
create trigger t_no_change_atomic_formation_enthalpy
before update of atomic_formation_enthalpy on items for statement
execute procedure f_no_change_atomic_formation_enthalpy();
---------------------------------------------------------------------------------------------

/*Test case everything is OK

truncate molecules, composition, items cascade;
insert into molecules values ('Fe3Sn1', 'Fe075Sn0.25'), ('Fe1Ni1', 'Fe0.5Sn0.5');
insert into composition values 	('Fe', 'Fe3Sn1', 0.75), ('Sn', 'Fe3Sn1', 0.25), 
				('Fe', 'Fe1Ni1', 0.5), ('Ni', 'Fe1Ni1', 0.5);

insert into items (	
type, name, summary, formula,
production_info,
compound_space_group, unit_cell_volume, lattice_parameters, lattice_angles, 
	atomic_positions, 
	crystal_info,
unit_cell_energy, unit_cell_formation_enthalpy, energy_info,
unit_cell_spin_polarization, atomic_spin_specie, saturation_magnetization, magnetization_temperature, magnetization_info,
	magnetocrystalline_anisotropy_energy, anisotropy_energy_type, magnetocrystalline_anisotropy_constants, kind_of_anisotropy, anisotropy_field, anisotropy_info,
	exchange_integrals,
	exchange_info, magnetic_order, 	curie_temperature, curie_temperature_info, remanence, coercivity, energy_product,
	hysteresis_info, domain_wall_width, domain_wall_info, exchange_stiffness, exchange_stiffness_info,
reference, comments
) values 
('E', 'Fe3Sn', 'Solid state reaction, crystal, anisotropy, magnetization, Tc', 'Fe3Sn1',
 '2 Solid State Reactions at 800ºC, for 48h',
  194, null, '[5.4621, 5.4621,4.3490]', null, 
	'[{"atom":"Fe1","vals":[0.8442,0.6912,"1/4"]},
	 {"atom":"Sn1","vals":["1/3","2/3","1/4"]}]',
	 'Cu-Kα radiation, room temperature, atmospheric pressure',
  null, null, null,
  null, null, 1.2, 300, null, 
	null, 'U', null, 'P', 2.5, null,
	null,
	null, 'ferromagnet', 747, null, null, null, null,
	null, null, null, null, null,
null, null
	 ),

('T', 'FeNi L1_0', 'crystal, energy, magnetization, anisotropy, exchange, Tc, domain wall width, exchange stiffness', 'Fe1Ni1',
 'obtained by AGA (software USPEX+VASP)',
 123, 22.7138, '[2.518,2.518,3.582]', '[90.0,90.0,90.0]',
  '[{"atom":"Fe1","vals":[0.0,0.0,0.0]},
   {"atom":"Ni1","vals":["1/2","1/2","1/2"]}]',
   'VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000,Ni_pv:PAW_PBE:06Sep2000. Pressure=0.0 kbar',
 -13.892258, -0.135758, 'VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000,Ni_pv:PAW_PBE:06Sep2000. Pressure=0.0 kbar',
 3.33, '[2.659,0.671]', 1.71, 0, 'VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000,Ni_pv:PAW_PBE:06Sep2000. Pressure=0.0 kbar', 
	'[[0,0,1,-13.94019080],[1,0,1, -13.94012990],[1,0,0,-13.94008791],[0,1,0,-13.94008803]]', 'U', '[0.73,0.0]', 'A', null, 'VASP, including spin-orbit coupling, 2816 k-points in IBZ, Ecut-off=400 eV, PAW_PBE,Fe_pv:PAW_PBE:06Sep2000,Ni_pv:PAW_PBE:06Sep2000. Pressure=0.0 kbar',
	'[{"symb1":"Fe","symb2":"Fe", "vals":[2.22119,1.35194,0.38785,0.50683,-0.67064,-0.4195]},
	  {"symb1":"Ni","symb2":"Ni", "vals":[0.29772,-0.04463,0.04442,0.04059,-0.01808,-0.00461]},
	  {"symb1":"Fe","symb2":"Ni", "vals":[1.27737,0.08671,0.03079,-0.02957,0.01218,0.0091]}
	 ]',
	'Fleur code, spin spirals (Fourier transform), kmax=4.1 a.u., 1960 k-points and 463 q-points, PBE. Pressure=0.0 kbar.', 'ferromagnet', 800, null, null, null, null,
	null, 14.1, 'ASD, T=5K', 15.3, 'It was calculated using well-known formula A=[〖δ_W/π]〗^2 K_1, where A is the exchange stiffness and δ_W is the domain wall width.', 
null, null	
	);

End test case everything is OK*/

/*Test cases for testing refused manullay updates on auxiliar properties

update items
set atomic_species=null;

update items
set species_count=null;

update items
set lattice_system=null;

update items
set unit_cell_atom_count=null;

update items
set atom_volume=null;

update items
set atomic_energy=null;

update items
set atomic_formation_enthalpy=null;

*/	
--select * from items; 

