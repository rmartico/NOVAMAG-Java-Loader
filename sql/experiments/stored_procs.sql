CREATE OR REPLACE FUNCTION f_atomic_species(int) RETURNS text AS $$

declare 
	result text;
begin
	select /*formula,*/ array_to_string(array_agg(symbol),',') atomic_species
	into result
	from items join composition using(formula)
	where mafId=$1;
	--group by mafId, formula;

	return result;
end;

$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION f_species_count(int) RETURNS int AS $$
declare 
	result int;
begin
	select /*formula,*/ count(symbol)
	into result
	from items join composition using(formula)
	where mafId=$1
	group by mafId, formula;

	return result;
end;

$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION f_lattice_system(int) RETURNS text AS $$
declare 
	result text;
begin
	select  case 
			when compound_space_group between 1 and 2 then 'TRIC'
			when compound_space_group between 3 and 15 then 'MON'
			when compound_space_group between 16 and 74 then 'ORT'
			when compound_space_group between 75 and 142 then 'TET'
			when compound_space_group in(146,148,155,160,161,166,167) then 'RHO'
			when compound_space_group between 143 and 145
				or compound_space_group=147
				or compound_space_group between 149 and 154
				or compound_space_group between 156 and 159
				or compound_space_group between 162 and 165
				or compound_space_group between 168 and 194 then 'HEX'
			when compound_space_group between 195 and 230 then 'CUB'
				
		end
	into result
	from items
	where mafId=$1;

	if (result is null) then	
		RAISE EXCEPTION 'Nonexistent compound space group in item --> %', $1 USING ERRCODE = '23505';-------------------------->PROBAR
	end if;

	return result;
end;

$$ LANGUAGE plpgsql;

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


CREATE OR REPLACE FUNCTION f_atom_volume(int) RETURNS numeric AS $$
declare 
	result numeric;

begin
	select unit_cell_volume/f_unit_cell_atom_count(formula)
	into result
	from items
	where mafId=$1;

	return result;
	
end;

$$ LANGUAGE plpgsql;

/*
This function version speeds up computation because its 2nd argument represente de unit cell atom count
which is supposed it has been previously calculated, saving the time in calling f_unit_cell_atom_count(formula)
*/
CREATE OR REPLACE FUNCTION f_atom_volume(int /*mafId*/, int /*unic_cell_atom_count*/) RETURNS numeric AS $$
declare 
	result numeric;

begin
	select unit_cell_volume/$2
	into result
	from items
	where mafId=$1;

	return result;
	
end;

$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION f_atomic_energy(int) RETURNS numeric AS $$
declare 
	result numeric;

begin
	select unit_cell_energy/f_unit_cell_atom_count(formula)
	into result
	from items
	where mafId=$1;

	return result;
	
end;

$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION f_atomic_formation_enthalpy(int) RETURNS numeric AS $$
declare 
	result numeric;

begin
	select unit_cell_formation_enthalpy/f_unit_cell_atom_count(formula)
	into result
	from items
	where mafId=$1;

	return result;
	
end;

$$ LANGUAGE plpgsql;


---	Unit cell atom count

/*
select formula, array_to_string(array_agg(symbol),',')
from items join composition using(formula)
group by mafId, formula;
*/

/*
select formula, (regexp_split_to_array(formula, '[A-Z]'))[2:]
,regexp_replace(unnest((regexp_split_to_array(formula, '[A-Z]'))[2:]), '[a-z]','') numb
	from items
order by 1;

with expanded as(
	select mafId, formula, (regexp_split_to_array(formula, '[A-Z]'))[2:],	
		regexp_replace(unnest((regexp_split_to_array(formula, '[A-Z]'))[2:]), '[a-z]','') numb
	from items )
select mafId, formula, sum (case numb 
				when '' then 1
				else cast(numb as integer)
			end)
from expanded
group by mafId, formula	
order by 1;
--------------------si llego aqui ya solo es hacer sum(numb) group by mafId

select regexp_replace(unnest((regexp_split_to_array('FeNi2', '[A-Z]'))[2:]), '[a-z]','') numb;

select sum (case numb 
			when '' then 1
			else cast(numb as integer)
		end)		
	from (select regexp_replace(unnest((regexp_split_to_array('FeNi2', '[A-Z]'))[2:]), '[a-z]','') numb) as t
*/

select mafId, formula, f_atomic_species(mafId), f_species_count(mafId), f_lattice_system(mafId), f_unit_cell_atom_count(formula),
unit_cell_volume, f_atom_volume(mafId), unit_cell_energy, type, f_atomic_energy(mafId), unit_cell_formation_enthalpy, f_atomic_formation_enthalpy(mafId)
from items;

create or replace function f_auxiliar_props() returns trigger as $$
	begin
		RAISE NOTICE 'mafId= %', new.mafId; 
		new.atomic_species := f_atomic_species(new.mafId);
		new.lattice_system := f_lattice_system(new.mafId);
		--new.atomic_species := 'Hola';
		RAISE NOTICE 'formula= %', new.formula; 
		RAISE NOTICE 'atomic_species= %', new.atomic_species; 
		RAISE NOTICE 'lattice system=%', new.lattice_system;

		return new;
	end;
$$
LANGUAGE plpgsql;


drop trigger if exists t_auxiliar_props on items;
create trigger t_auxiliar_props
before insert or update of formula on items
for each row
execute procedure f_auxiliar_props();

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
	/*null,*/ 'Cu-Kα radiation, room temperature, atmospheric pressure',
  null, null, null,
  null, null, 1.2, 300, null, 
	null, 'U'/*niaxial'*/, null, 'P'/*'planar'*/, 2.5, null,
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
  /*true,*/ 'VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000,Ni_pv:PAW_PBE:06Sep2000. Pressure=0.0 kbar',
 -13.892258, -0.135758, 'VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000,Ni_pv:PAW_PBE:06Sep2000. Pressure=0.0 kbar',
 3.33, '[2.659,0.671]', 1.71, 0, 'VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000,Ni_pv:PAW_PBE:06Sep2000. Pressure=0.0 kbar', 
	'[[0,0,1,-13.94019080],[1,0,1, -13.94012990],[1,0,0,-13.94008791],[0,1,0,-13.94008803]]', 'U'/*niaxial'*/, '[0.73,0.0]', /*??'uniaxial'*/'A', null, 'VASP, including spin-orbit coupling, 2816 k-points in IBZ, Ecut-off=400 eV, PAW_PBE,Fe_pv:PAW_PBE:06Sep2000,Ni_pv:PAW_PBE:06Sep2000. Pressure=0.0 kbar',
	'[{"symb1":"Fe","symb2":"Fe", "vals":[2.22119,1.35194,0.38785,0.50683,-0.67064,-0.4195]},
	  {"symb1":"Ni","symb2":"Ni", "vals":[0.29772,-0.04463,0.04442,0.04059,-0.01808,-0.00461]},
	  {"symb1":"Fe","symb2":"Ni", "vals":[1.27737,0.08671,0.03079,-0.02957,0.01218,0.0091]}
	 ]',
	'Fleur code, spin spirals (Fourier transform), kmax=4.1 a.u., 1960 k-points and 463 q-points, PBE. Pressure=0.0 kbar.', 'ferromagnet', 800, null, null, null, null,
	null, 14.1, 'ASD, T=5K', 15.3, 'It was calculated using well-known formula A=[〖δ_W/π]〗^2 K_1, where A is the exchange stiffness and δ_W is the domain wall width.', 
null, null	
	);


	
select * from items; 

