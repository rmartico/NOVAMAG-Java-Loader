/*
4-NOV
Changes version v07:
UPDATE/DELETE CASCADE of FKs pointing MafIf

24-OCT-2017
In this version
a.1) Has a new field: stechiometry
a.2) In the formula field the atoms are stored in alphabetical order, even if the user has input it in another order

25-OCT-2017
b) Materials table dissapears

25-OCT-2017
c) The serial field MafId in items is kept because is easier for FKs
   However, the old materials.name field is moved to items as unique not null
   //TODO A trigger checks unique violation and concatenate a serial number behind the intended name in that case

   --------------------
   1) En la propiedad "name" sería conveniente añadir el valor de space group seguido de un número que evite duplicidades por ejemplo: 

	"name": "novamag_theory_ICCRAM_Fe1Ni1_#123_1"

	el autor podria ir delante del nombre del compuesto.
   --------------------
25-OCT-2017
d) Authors is a new table

26-OCT-2017
e) the attached files are included as byte[]

f) //TODO
En "atomic positions" sería conveniente opcionalmente poder añadir el nombre específico del lugar que ocupa el átomo en la estructura (quizás entre parentesis). Por ejemplo
      "atomic positions": {
        "value": [
          "Mn1(1a)",
          0.0,
          0.0,
          0.0,
          "Al1(1d)",
          0.5,
          0.5,
          0.5
        ]
      },

 g)//TODO
 En la propiedad "atomic spin specie" Sergiu sugiere utilizar el siguiente  formato 
      "atomic spin specie": {
        "value": ["1 Mn",2.352,"2 Al",-0.056]
      },

*/

drop schema if exists public cascade;
create schema public;
set search_path to public;

create table atoms(
	symbol varchar(2) primary key
);

INSERT INTO atoms VALUES ('Al'), ('Co'), ('Fe'), ('Ge'), ('Ni'), ('Mn'), ('Sb'), ('Sn'), ('Ta');

create table molecules( --Co2P1, Co4P2 & Co0.67P0.33 are considered different material formulas
	formula 	varchar(20) primary key, 	--The formula elements are stored in alphabetical order
	stechiometry	varchar(30) not null		--It is not unq because different materials can share the same stechiometry
);
insert into molecules values ('Fe3Sn1', 'Fe075Sn0.25'), ('Fe1Ni1', 'Fe0.5Sn0.5');

create table composition(
	symbol 			varchar(2) 	references atoms,
	formula			varchar(20)	references molecules,
	numb_of_occurrences	numeric(4,3),	--stoichiometric composition

	primary key(symbol, formula) 	
);

insert into composition values 	('Fe', 'Fe3Sn1', 0.75), ('Sn', 'Fe3Sn1', 0.25), 
				('Fe', 'Fe1Ni1', 0.5), ('Ni', 'Fe1Ni1', 0.5);
				
create table magnetic_orders(
	magnetic_order	varchar(20) primary key
);
insert into magnetic_orders values
('ferromagnet'), ('antiferromagnet'), ('ferrimagnet'), ('paramagnet'), ('diamagnet');

create table items(
	mafId	serial primary key,
	type	char(1) not null check (type in ('E','T')),
	name	varchar(50) unique not null,
	summary text, --change varchar(255),

	--Chemistry
	formula			varchar(20)	references molecules not null,
	production_info		varchar(255),

	--Crystal
	compound_space_group	smallInt     check(compound_space_group between 1 and 230),
	unit_cell_volume	numeric(9,4) check (unit_cell_volume between 0 and 100000),
	lattice_parameters	json, 
	lattice_angles		json, 
	atomic_positions	json, 
	crystal_info		text, 

	--Thermodynamics
	unit_cell_energy		numeric(12,7) check( unit_cell_energy between -100000 and 100000), 
	unit_cell_formation_enthalpy	numeric(10,6) check( unit_cell_formation_enthalpy between -1000 and 1000), 
	energy_info			text, 
	interatomic_potentials_info	text, 
	magnetic_free_energy		numeric(11,6) check( magnetic_free_energy between -100000 and 100000),
	magnetic_free_energy_info	text,


	--Magnetics
	unit_cell_spin_polarization	numeric( 11,6) check(unit_cell_spin_polarization between 0 and 10000), 
	atomic_spin_specie 		json,
	saturation_magnetization	numeric(6, 3) check (saturation_magnetization between 0 and 100),
	magnetization_temperature	numeric(8,3) check (magnetization_temperature between 0 and 100000),
	magnetization_info		text, 
	magnetocrystalline_anisotropy_energy 	json, 
	anisotropy_energy_type 		char(1) check(anisotropy_energy_type in ('U'/*uniaxial*/,'C'/*cubic*/,'P'/*planar*/)),	--CHANGE
	magnetocrystalline_anisotropy_constants	json, 
	kind_of_anisotropy		char(1) check( kind_of_anisotropy in ('A'/*easy axis*/,'P'/*"planar easy axis"*/,'C'/*easy cone*/)),	--CHANGE
	anisotropy_info			varchar(255),
	exchange_integrals		json,
	exchange_info			text,
	magnetic_order			varchar(20) references magnetic_orders,
	curie_temperature		numeric(8,3) check (curie_temperature between 0 and 10000),
	curie_temperature_info		text,
	anisotropy_field		numeric(5,2) check(anisotropy_field between 0 and 100),
	remanence			numeric(6,3) check(remanence between 0 and 100),
	coercivity			numeric(6,3) check(coercivity between 0 and 100),
	energy_product			numeric(8,3) check(energy_product between 0 and 10000),
	hysteresis_info 		text, 
	domain_wall_width		numeric(7,3) check(domain_wall_width between 0 and 1000),
	domain_wall_info		text, 
	exchange_stiffness 		numeric( 7, 3) check(exchange_stiffness between 0 and 1000),
	exchange_stiffness_info		text, 

	--Additional information	
	reference			varchar(255),
	comments			text,

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

create table authors(
	author varchar(80) primary key
);

insert into authors values ('ICCRAM'), ('BCM');

create table authoring(
	author varchar(80) references authors,
	mafId  integer     references items on delete cascade on update cascade,
	primary key( author, mafId )	
);

insert into items (	
type, name, summary, formula,
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

insert into authoring values ('BCM', 1),('ICCRAM',2);


--attached files
create table file_types(
	file_type varchar(20) unique,
	is_text boolean,
	regExp varchar(50) not null,
	primary key( file_type, is_text)
);
insert into file_types values 
--('POSCAR', true), ('DOSCAR',true),
('CONTCAR',true, 'CONTCAR.*'),
('CIF', true,  '.*\.(cif|CIF)'),
('JPG', false, '.*\.(jpg|JPG|jpeg|JPEG)');

create extension if not exists lo; --Extension to deal with BLOBs (e.g. jpeg)

create table attached_files(
	mafId  		integer references items on delete cascade on update cascade,
	file_name	varchar(50),
	file_type 	varchar(20) not null,
	is_text 	boolean not null,
	blob_content	bytea,
	info		text,
	foreign key ( file_type, is_text) references file_types, 
	primary key ( mafId, file_name )
);


--insert into attached_files values ( ...

--General
 select mafId, type, name, summary
 from items;

--Chemistry
 select mafId, type, name,
  formula, production_info
 from items natural join molecules;

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

--Additional information
select mafId, type, name, 
author, reference, comments,
file_name, file_type, is_text, blob_content, info	
from items join authoring using(mafId)
left join attached_files using(mafId);

--Delete example rows
truncate attached_files, items, /*materials,*/ composition, molecules, authors, authoring cascade;