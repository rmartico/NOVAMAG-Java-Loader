/*
It includes auxiliar properties:

table Molecules
-Stoichiometry: Molecules.Stoichiometry was yet included in previous versions

table Items
-Atomic species
-Species count
-Lattice system
-Unit cell atom count
-Atom volume
-Atomic energy
-Atomic formation enthalpy
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

create table composition(
	symbol 			varchar(2) 	references atoms,
	formula			varchar(20)	references molecules,
	numb_of_occurrences	numeric(4,3),	--stoichiometric composition

	primary key(symbol, formula) 	
);

				
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
	atomic_species		varchar(255),   --auxiliar properties: Comma separated list of species in the material (from formula)
	species_count		integer,	--auxiliar properties: number of distinct species in the material (from formula)

	--Crystal
	compound_space_group	smallInt     check(compound_space_group between 1 and 230),
	lattice_system	 	varchar(3),	--auxiliar properties: derived from space group
	unit_cell_atom_count	smallint,       --auxiliar properties: number of atoms in the unit cell (from formula)

	unit_cell_volume	numeric(9,4) check (unit_cell_volume between 0 and 100000),
	atom_volume		numeric(9,4),    --auxiliar properties: Unit cell volume”/ ”Unit cell atom count
	lattice_parameters	json, 
	lattice_angles		json, 
	atomic_positions	json, 
	crystal_info		text, 
	
	--Thermodynamics
	unit_cell_energy		numeric(12,7) check( unit_cell_energy between -100000 and 100000), 
	atomic_energy			numeric(10,7), --auxiliar properties: Unit cell energy/ Unit cell atom count
	unit_cell_formation_enthalpy	numeric(10,6) check( unit_cell_formation_enthalpy between -1000 and 1000), 
	atomic_formation_enthalpy	numeric(11,7),  --auxiliar properties: Unit cell formation enthalpy/Unit cell atom count
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


