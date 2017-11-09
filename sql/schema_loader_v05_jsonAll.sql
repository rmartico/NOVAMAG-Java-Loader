drop schema if exists public cascade;
create schema public;
set search_path to public;

create table atoms(
	symbol varchar(2) primary key
);
insert into atoms values ('Fe'), ('Ni'), ('Sn');

create table molecules( --Co2P1, Co4P2 & Co0.67P0.33 are considered different material formulas
	formula 	varchar(20) primary key 	--The formula elements are kept in the same order as entered by the user --TOASK
);
insert into molecules values ('Fe3Sn1'), ('Fe1Ni1');

create table composition(
	symbol 			varchar(2) 	references atoms,
	formula			varchar(20)	references molecules,
	numb_of_occurrences	numeric(4,3),	--stoichiometric composition

	primary key(symbol, formula) 	
);

insert into composition values 	('Fe', 'Fe3Sn1', 0.75), ('Sn', 'Fe3Sn1', 0.25), 
				('Fe', 'Fe1Ni1', 0.5), ('Ni', 'Fe1Ni1', 0.5);


create table materials(
	name	varchar(50) primary key,
	formula	varchar(20) references molecules not null
);
insert into materials values ( 'Fe3Sn', 'Fe3Sn1'), ('FeNi L1_0', 'Fe1Ni1');

create table magnetic_orders(
	magnetic_order	varchar(20) primary key
);
insert into magnetic_orders values
('ferromagnet'), ('antiferromagnet'), ('ferrimagnet'), ('paramagnet'), ('diamagnet');

create table items(
	mafId	serial primary key,
	type	char(1) not null check (type in ('E','T')),
	name	varchar(50) not null references materials,
	summary varchar(255),

	--Chemistry
	--formula			varchar(20)	references molecules not null,
	production_info		varchar(255),

	--Crystal
	compound_space_group	smallInt	/*change not null*/ check(compound_space_group between 1 and 230),
	unit_cell_volume	numeric(9,4) check (unit_cell_volume between 0 and 100000),--change numeric(6,4),
	lattice_parameters	json, --numeric(7,6)[3], => change (validation rule) numeric(11,6)[3] between 0 and 100000
	lattice_angles		json, --numeric(9,6)[3], => change (validation rule) numeric(6,3)[3] check (lattice_angles[i] between 0 and 360
	atomic_positions	json, --[]:Number of elements (<=numAtoms) [3] dimensions [2] numerator/denominator	--Validation rule: The number of elements must match the number of elements in the formula
	--change known_phase		boolean,
	crystal_info		text, --change varchar(255), text is the PostgreSQL type for CLOB

	--Thermodynamics
	unit_cell_energy		numeric(12,7) check( unit_cell_energy between -100000 and 100000), --change numeric(8,6),
	unit_cell_formation_enthalpy	numeric(10,6) check( unit_cell_formation_enthalpy between -1000 and 1000), --change numeric(8,6),
	energy_info			text, --change varchar(255), text is the PostgreSQL type for CLOB
	interatomic_potentials_info	text, --change: new field
	magnetic_free_energy		numeric(11,6) check( magnetic_free_energy between -100000 and 100000), --change: new field
	magnetic_free_energy_info	text, --change: new field


	--Magnetics
	unit_cell_spin_polarization	numeric( 11,6) check(unit_cell_spin_polarization between 0 and 10000), --change numeric(6,5),
	atomic_spin_specie 		json, --numeric(4,3)[], array dimension = total number of atoms in the molecule. change: To be treated as text.
	saturation_magnetization	numeric(6, 3) check (saturation_magnetization between 0 and 100), --change numeric(3,2),
	magnetization_temperature	numeric(8,3) check (magnetization_temperature between 0 and 100000),--change numeric(4,1),
	magnetization_info		text, --change varchar(255),	
	magnetocrystalline_anisotropy_energy 	json, --numeric(12,10)[][4],
	anisotropy_energy_type 		char(1) check(anisotropy_energy_type in ('U'/*uniaxial*/,'C'/*cubic*/)),	--change	varchar(20), 
	magnetocrystalline_anisotropy_constants	json, --change (validation rule) numeric(6,3)[] betweem -100 and 100,
	kind_of_anisotropy		char(1) check( kind_of_anisotropy in ('A'/*easy axis*/,'P'/*easy plane*/,'C'/*easy cone*/)),	--change varchar(20), 
	anisotropy_info			varchar(255),
	exchange_integrals		json,
	exchange_info			text, --change varchar(255),
	magnetic_order			varchar(20) references magnetic_orders, --change new foregn key
	curie_temperature		numeric(8,3) check (curie_temperature between 0 and 10000), --change numeric(5,1),
	curie_temperature_info		text, --change varchar(255),	
	anisotropy_field		numeric(5,2) check(anisotropy_field between 0 and 100),
	remanence			numeric(6,3) check(remanence between 0 and 100),--change numeric(5,1),	
	coercivity			numeric(6,3) check(coercivity between 0 and 100),--change numeric(5,1),
	energy_product			numeric(8,3) check(energy_product between 0 and 10000),--change numeric(5,1), 
	hysteresis_info 		text, --change varchar(255),
	domain_wall_width		numeric(7,3) check(domain_wall_width between 0 and 1000),--change numeric(5,1), 
	domain_wall_info		text, --change varchar(255),
	exchange_stiffness 		numeric( 7, 3) check(exchange_stiffness between 0 and 1000), --change numeric(5,1), 
	exchange_stiffness_info		text, --change varchar(255),

	--Additional information
	authors				json, --varchar(80)[],
	reference			varchar(255),
	comments			text, --change varchar(255),


	check ((type='E' /*change and known_phase 				is null*/
			 and unit_cell_energy				is null
			 and interatomic_potentials_info		is null --change
			 and magnetic_free_energy			is null --change
			 and magnetic_free_energy_info			is null --change
			 and unit_cell_spin_polarization		is null
			 and magnetocrystalline_anisotropy_energy 	is null
			 and exchange_integrals				is null
			 and exchange_info				is null
			 )
	 or
	       (type='T' /*change and known_phase */	
			 /*change and unit_cell_energy				is not null --?*/
			 /*change and unit_cell_spin_polarization		is not null --?*/
			 /*change and magnetocrystalline_anisotropy_energy	is not null --?*/
			 /*change and exchange_integrals				is not null --?*/
			 /*change and exchange_info				is not null --?*/
			 )
	)
	
	
);



insert into items (	
type, name, summary, 
production_info,
compound_space_group, unit_cell_volume, lattice_parameters, lattice_angles, 
	atomic_positions, 
	/*change known_phase,*/ crystal_info,
unit_cell_energy, unit_cell_formation_enthalpy, energy_info,
unit_cell_spin_polarization, atomic_spin_specie, saturation_magnetization, magnetization_temperature, magnetization_info,
	magnetocrystalline_anisotropy_energy, anisotropy_energy_type, magnetocrystalline_anisotropy_constants, kind_of_anisotropy, anisotropy_field, anisotropy_info,
	exchange_integrals,
	exchange_info, magnetic_order, 	curie_temperature, curie_temperature_info, remanence, coercivity, energy_product,
	hysteresis_info, domain_wall_width, domain_wall_info, exchange_stiffness, exchange_stiffness_info,
authors, reference, comments
) values 
('E', 'Fe3Sn', 'Solid state reaction, crystal, anisotropy, magnetization, Tc',
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
'["BCM"]', null, null
	 ),


('T', 'FeNi L1_0', 'crystal, energy, magnetization, anisotropy, exchange, Tc, domain wall width, exchange stiffness',
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
'["ICCRAM"]', null, null	
	);

--attached files
create table file_types(
	file_type varchar(20) primary key
);
insert into file_types values ('POSCAR'), ('DOSCAR');

create table attached_files(
	mafId  		integer references items,
	file_type 	varchar(20) references file_types,
	content		bytea not null,
	info		text, --change varchar(255),
	primary key ( mafId, file_type )
);

--insert into attached_files values ( ...

--General
 select mafId, type, name, summary
 from items;

--Chemistry
 select mafId, type, name,
  formula, production_info
 from items natural join materials;

 --Crystal	
  select mafId, type, name,
  compound_space_group, unit_cell_volume, lattice_parameters, lattice_angles, atomic_positions,   
  /*case known_phase
	when true then 'yes'
	when false then 'no'
  end,*/
  crystal_info  
  from items;

--Thermodynamics
select mafId, type, name,
unit_cell_energy, unit_cell_formation_enthalpy, energy_info,
interatomic_potentials_info, magnetic_free_energy, magnetic_free_energy_info	--change: 3 new fields
from items;

--Magnetics	(problems to represent magnetocrystalline_anisotropy_energy properly => nested array, what rule do obey the 3 first components in the inner array (i.e. bin(0...8))? ...)
select mafId, type, name,
unit_cell_spin_polarization, atomic_spin_specie, saturation_magnetization, magnetization_temperature, magnetization_info,
magnetocrystalline_anisotropy_energy, 
case anisotropy_energy_type
	when 'U' then 'uniaxial'
	when 'C' then 'cubic'
end, magnetocrystalline_anisotropy_constants, 
case kind_of_anisotropy
	when 'A' then 'easy axis'
	when 'P' then 'easy plane'
	when 'C' then 'easy cone'
end,  anisotropy_info,
exchange_integrals, exchange_info, magnetic_order, curie_temperature, curie_temperature_info, anisotropy_field/*change of position in the form*/, remanence, coercivity, energy_product,
hysteresis_info, domain_wall_width, domain_wall_info, exchange_stiffness, exchange_stiffness_info
from items;

--Additional information ... Pending to include attached files info
select mafId, type, name, 
authors, reference, comments
from items;

--Delete example rows
truncate attached_files, items, materials, composition, molecules;