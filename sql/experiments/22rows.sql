--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.3
-- Dumped by pg_dump version 9.6.3

-- Started on 2017-11-01 20:58:46

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = public, pg_catalog;

truncate attached_files, items, atoms, composition, molecules, authors, authoring, file_types, magnetic_orders cascade;

--
-- TOC entry 2352 (class 0 OID 38051)
-- Dependencies: 235
-- Data for Name: atoms; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO atoms VALUES ('Al');
INSERT INTO atoms VALUES ('Co');
INSERT INTO atoms VALUES ('Fe');
INSERT INTO atoms VALUES ('Ge');
INSERT INTO atoms VALUES ('Ni');
INSERT INTO atoms VALUES ('Mn');
INSERT INTO atoms VALUES ('Sb');
INSERT INTO atoms VALUES ('Sn');
INSERT INTO atoms VALUES ('Ta');


--
-- TOC entry 2360 (class 0 OID 38143)
-- Dependencies: 243
-- Data for Name: file_types; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO file_types VALUES ('CONTCAR', true, 'CONTCAR.*');
INSERT INTO file_types VALUES ('CIF', true, '.*\.(cif|CIF)');
INSERT INTO file_types VALUES ('JPG', false, '.*\.(jpg|JPG|jpeg|JPEG)');


--
-- TOC entry 2355 (class 0 OID 38076)
-- Dependencies: 238
-- Data for Name: magnetic_orders; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO magnetic_orders VALUES ('ferromagnet');
INSERT INTO magnetic_orders VALUES ('antiferromagnet');
INSERT INTO magnetic_orders VALUES ('ferrimagnet');
INSERT INTO magnetic_orders VALUES ('paramagnet');
INSERT INTO magnetic_orders VALUES ('diamagnet');


--
-- TOC entry 2353 (class 0 OID 38056)
-- Dependencies: 236
-- Data for Name: molecules; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO molecules VALUES ('FeNi', 'Fe0.500Ni0.500');
INSERT INTO molecules VALUES ('AlMn', 'Al0.500Mn0.500');
INSERT INTO molecules VALUES ('CoFe4Ta', 'Co0.167Fe0.667Ta0.167');
INSERT INTO molecules VALUES ('Fe3Sn', 'Fe0.750Sn0.250');
INSERT INTO molecules VALUES ('Fe9Mn3SbSn3', 'Fe0.563Mn0.188Sb0.063Sn0.188');
INSERT INTO molecules VALUES ('Fe8Mn4SbSn3', 'Fe0.500Mn0.250Sb0.063Sn0.188');
INSERT INTO molecules VALUES ('Fe6Mn6SbSn3', 'Fe0.375Mn0.375Sb0.063Sn0.188');
INSERT INTO molecules VALUES ('Fe15Mn15SbSn9', 'Fe0.375Mn0.375Sb0.025Sn0.225');
INSERT INTO molecules VALUES ('Fe3Ge', 'Fe0.750Ge0.250');
INSERT INTO molecules VALUES ('Fe4Ge', 'Fe0.800Ge0.200');
INSERT INTO molecules VALUES ('Fe5Ge', 'Fe0.833Ge0.167');
INSERT INTO molecules VALUES ('Fe5Ta', 'Fe0.833Ta0.167');
INSERT INTO molecules VALUES ('Fe6Ge', 'Fe0.857Ge0.143');
INSERT INTO molecules VALUES ('Fe8Ta2', 'Fe0.800Ta0.200');
INSERT INTO molecules VALUES ('Fe10Ta2', 'Fe0.833Ta0.167');
INSERT INTO molecules VALUES ('Fe12Ge6', 'Fe0.667Ge0.333');
INSERT INTO molecules VALUES ('Fe14Ta2', 'Fe0.875Ta0.125');
INSERT INTO molecules VALUES ('Fe16Ta4', 'Fe0.800Ta0.200');
INSERT INTO molecules VALUES ('Fe20Ta4', 'Fe0.833Ta0.167');


--
-- TOC entry 2357 (class 0 OID 38083)
-- Dependencies: 240
-- Data for Name: items; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO items VALUES (1, 'T', 'novamag_theory_ICCRAM_Fe1Ni1_#123_1', 'AGA, crystal, energy, magnetization, anisotropy, exchange, Tc, domain wall width, exchange stiffness', 'FeNi', 'obtained by Adaptive Genetic Algorithm,  software USPEX+VASP', 123, 22.7138, '[2.518,2.518,3.582]', '[90,90,90]', '["Fe1(1a)",0,0,0,"Ni1(1d)",0.5,0.5,0.5]', 'software VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000, Ni_pv:PAW_PBE:06Sep2000,Pressure=0.0 kbar,temperature=0K', -13.8922580, -0.135758, 'software VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000, Ni_pv:PAW_PBE:06Sep2000, Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 3.330000, '["1 Fe",2.659,"2 Ni",0.671]', 1.710, 0.000, 'software VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000, Ni_pv:PAW_PBE:06Sep2000, Pressure=0.0 kbar', '[0,0,1,-13.9401908,1,0,1,-13.9401299,1,0,0,-13.94008791,0,1,0,-13.94008803]', 'U', '[0.73,0]', 'A', 'software VASP, including spin-orbit coupling, 2816 k-points in IBZ, Ecut-off=400 eV, PAW_PBE, Fe_pv:PAW_PBE:06Sep2000, Ni_pv:PAW_PBE:06Sep2000, Pressure=0.0 kbar', '["Fe-Fe",2.22119,1.35194,0.38785,0.50683,-0.67064,-0.4195,"Ni-Ni",0.29772,-0.04463,0.04442,0.04059,-0.01808,-0.00461,"Fe-Ni",1.27737,0.08671,0.03079,-0.02957,0.01218,0.0091]', 'software Fleur, spin spirals (Fourier transform), kmax=4.1 a.u., 1960 k-points and 463 q-points, PBE, Pressure=0.0 kbar', 'ferromagnet', 800.000, 'software UppASD, Atomistic Spin Dynamics, system size 40x40x40 spins, periodic boundary conditions', 1.07, 1.420, 1.750, 352.230, 'software OOMMF, Micromagnetics, bulk', 14.400, 'Atomistic Spin Dynamics, system size 300x40x40 spins, temperature=5K', 15.300, 'It was calculated using well-known formula where A is the exchange stiffness', NULL, NULL);
INSERT INTO items VALUES (2, 'T', 'novamag_theory_ICCRAM_Mn1Al1_#123_1', 'AGA, crystal, energy, magnetization, anisotropy, exchange, Tc, domain wall width, exchange stiffness', 'AlMn', 'obtained by Adaptive Genetic Algorithm,  software USPEX+VASP', 123, 26.3580, '[2.755,2.755,3.473]', '[90,90,90]', '["Mn1(1a)",0,0,0,"Al1(1d)",0.5,0.5,0.5]', 'software VASP, k-points mesh 15x15x12, Ecut-off=400 eV, PAW_PBE, Mn_pv:PAW_PBE:06Sep2000, Al:PAW_PBE:06Sep2000,Pressure=0.0 kbar,temperature=0K', -13.2583620, NULL, 'software VASP, k-points mesh 15x15x12, Ecut-off=400 eV, PAW_PBE, Mn_pv:PAW_PBE:06Sep2000, Al:PAW_PBE:06Sep2000,Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 2.296000, '["1 Mn",2.352,"2 Al",-0.056]', 1.010, 0.000, 'software VASP, k-points mesh 15x15x12, Ecut-off=400 eV, PAW_PBE, Mn_pv:PAW_PBE:06Sep2000, Al:PAW_PBE:06Sep2000,Pressure=0.0 kbar,temperature=0K', '[0,0,1,-13.27359703,1,0,1,-13.27347226,1,0,0,-13.27334379]', 'U', '[1.51,0]', 'A', 'software VASP, including spin-orbit coupling, 2816 k-points in IBZ, Ecut-off=400 eV, PAW_PBE, Mn_pv:PAW_PBE:06Sep2000, Al:PAW_PBE:06Sep2000,Pressure=0.0 kbar,temperature=0K', '["Mn-Mn",2.5542,1.53838,1.10922,-0.54833,0.38383,-0.13525,0.1214,0.11231,0.1122,"Al-Al",-0.03054,0.05352,-0.00119,"Mn-Al",0.0967,-0.06642,-0.06491,0.02344,0.02341]', 'software Fleur, spin spirals (Fourier transform), kmax=4.1 a.u., 1089 k-points and 441 q-points, PBE, Pressure=0.0 kbar', 'ferromagnet', 660.000, 'software UppASD, Atomistic Spin Dynamics, system size 40x40x40 spins, periodic boundary conditions,up to 5 nearest neighbors exchange interactions', NULL, NULL, NULL, NULL, NULL, 9.200, 'Atomistic Spin Dynamics, system size 300x40x40 spins, temperature=5K', 12.950, 'Atomistic Spin Dynamics, system size 300x40x40 spins, temperature=5K', NULL, NULL);
INSERT INTO items VALUES (3, 'T', 'novamag_theory_ICCRAM_Co1Fe4Ta1_#160_1', 'AGA, crystal, energy, magnetization', 'CoFe4Ta', 'obtained by Adaptive Genetic Algorithm,  software USPEX+VASP', 160, 74.3605, '[4.72538,4.72539,4.72538]', '[59.8527,59.8528,59.8528]', NULL, 'software VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Co:02Aug2007, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:07Sep2000 ,Pressure=0.0 kbar,temperature=0K', -52.5427000, -0.108100, 'software VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Co:02Aug2007, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:07Sep2000 ,Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 9.192000, '["1 Co",1.124,"2 Fe",1.905,"3 Fe",1.79,"4 Fe",1.988,"5 Fe",2.688,"6 Ta",-0.456]', 1.440, 0.000, 'software VASP, k-points mesh 16x16x11, Ecut-off=400 eV, PAW_PBE, Co:02Aug2007, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:07Sep2000 ,Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (4, 'E', 'novamag_experiment_BCM_Fe3Sn_#194_1', 'synthesis conditions; experimental crystallographic information: space group, lattice parameters, atomic positions, conditions for (XRD) measurement; experimental magnetic information: kind of anisotropy, curie temperature, saturation magnetization, anisotropy field, coercivity', 'Fe3Sn', '2 Solid State Reactions at 800ºC, for 48h', 194, 112.3671, '[5.4621,5.4621,4.349]', '[90,90,120]', '["Fe1",0.8442,0.6912,0.25,"Sn1",0.3333,0.6666,0.25]', 'Conditions for (XRD) measurement: room temperature, atmospheric pressure, Cu-K alpha radiation', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1.330, 300.000, NULL, NULL, 'P', NULL, 'P', NULL, NULL, NULL, 'ferromagnet', 747.150, NULL, 2.50, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (5, 'E', 'novamag_experiment_BCM_Fe3Sn_#194_2', 'synthesis conditions; experimental crystallographic information: space group, lattice parameters, atomic positions, conditions for (XRD) measurement; experimental magnetic information: kind of anisotropy, curie temperature, saturation magnetization, anisotropy field, coercivity', 'Fe3Sn', 'Mechanical alloying + heat treatment 800ºC, 48 hours', 194, 112.4873, '[5.4637,5.4637,4.3511]', '[90,90,120]', '["Fe1",0.8552,0.6923,0.25,"Sn1",0.3333,0.6666,0.25]', 'Conditions for (XRD) measurement: room temperature, atmospheric pressure, Cu-K alpha radiation', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1.220, 300.000, NULL, NULL, 'P', NULL, 'P', NULL, NULL, NULL, 'ferromagnet', 745.850, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (6, 'E', 'novamag_experiment_BCM_Fe9Mn3Sn3Sb1_#194_1', 'synthesis conditions; experimental crystallographic information: space group, lattice parameters, atomic positions, conditions for (XRD) measurement; experimental magnetic information: kind of anisotropy, curie temperature, saturation magnetization, anisotropy field, coercivity', 'Fe9Mn3SbSn3', '2 Solid State Reactions at 800ºC, for 48h', 194, 113.9464, '[5.4858,5.4858,4.3721]', '[90,90,120]', '["Fe1",0.8442,0.6912,0.25,"Sn1",0.3333,0.6666,0.25]', 'Conditions for (XRD) measurement: room temperature, atmospheric pressure, Cu-K alpha radiation', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'P', NULL, 'P', NULL, NULL, NULL, 'ferromagnet', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (7, 'E', 'novamag_experiment_BCM_Fe9Mn3Sn3Sb1_#194_2', 'synthesis conditions; experimental crystallographic information: space group, lattice parameters, atomic positions, conditions for (XRD) measurement; experimental magnetic information: kind of anisotropy, curie temperature, saturation magnetization, anisotropy field, coercivity', 'Fe9Mn3SbSn3', '2 Solid State Reactions at 800ºC, for 48h', 194, 115.7108, '[5.4858,5.4858,4.4398]', '[90,90,120]', '["Fe1",0.8553,0.6975,0.25,"Sn1",0.3333,0.6666,0.25]', 'Conditions for (XRD) measurement: room temperature, atmospheric pressure, Cu-K alpha radiation', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'P', NULL, 'P', NULL, NULL, NULL, 'ferromagnet', NULL, '310.75', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (8, 'E', 'novamag_experiment_BCM_Fe8Mn4Sn3Sb1_#194_1', 'synthesis conditions; experimental crystallographic information: space group, lattice parameters, atomic positions, conditions for (XRD) measurement; experimental magnetic information: kind of anisotropy, curie temperature, saturation magnetization, anisotropy field, coercivity', 'Fe8Mn4SbSn3', '2 Solid State Reactions at 800ºC, for 48h', 194, 114.8200, '[5.5,5.5,4.3829]', '[90,90,120]', '["Fe1",0.8442,0.6912,0.25,"Sn1",0.3333,0.6666,0.25]', 'Conditions for (XRD) measurement: room temperature, atmospheric pressure, Cu-K alpha radiation', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'P', NULL, 'P', NULL, NULL, NULL, 'ferromagnet', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (9, 'E', 'novamag_experiment_BCM_Fe6Mn6Sn3Sb1_#194_1', 'synthesis conditions; experimental crystallographic information: space group, lattice parameters, atomic positions, conditions for (XRD) measurement; experimental magnetic information: kind of anisotropy, curie temperature, saturation magnetization, anisotropy field, coercivity', 'Fe6Mn6SbSn3', '2 Solid State Reactions at 800ºC, for 48h', 194, 114.8200, '[5.5338,5.5338,4.427]', '[90,90,120]', '["Fe1",0.8789,0.6973,0.25,"Sn1",0.3333,0.6666,0.25]', 'Conditions for (XRD) measurement: room temperature, atmospheric pressure, Cu-K alpha radiation', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'U', NULL, 'A', NULL, NULL, NULL, 'ferromagnet', 404.450, NULL, 1.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (10, 'E', 'novamag_experiment_BCM_Fe15Mn15Sn9Sb1_#194_1', 'synthesis conditions; experimental crystallographic information: space group, lattice parameters, atomic positions, conditions for (XRD) measurement; experimental magnetic information: kind of anisotropy, curie temperature, saturation magnetization, anisotropy field, coercivity', 'Fe15Mn15SbSn9', '1 Solid State Reaction at 800ºC, for 48h', 194, 118.7741, '[5.5545,5.5545,4.4453]', '[90,90,120]', '["Fe1",0.8442,0.6912,0.25,"Sn1",0.3333,0.6666,0.25]', 'Conditions for (XRD) measurement: room temperature, atmospheric pressure, Cu-K alpha radiation', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'U', NULL, 'A', NULL, NULL, NULL, 'ferromagnet', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (11, 'T', 'NOVAMAG_theory_ICCRAM_Fe3Ge1_#225_1', 'AGA, crystal, energy, magnetization', 'Fe3Ge', 'obtained by Adaptive Genetic Algorithm Structure Prediction Methods,  software USPEX+VASP', 225, 46.6293, '[5.71355,5.71355,5.71355]', '[90,90,90]', '["Fe1(8c)",0.25,0.25,0.25,"Fe2(4b)",0.5,0.5,0.5,"Ge1(4a)",0,0,0]', 'software VASP, k-points mesh 12x12x12, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar,temperature=0K', -29.6827096, -0.417957, 'software VASP, k-points mesh 12x12x12, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 5.625000, '["1 Fe",1.558,"2 Fe",1.558,"3 Fe",2.585,"4 Ge",-0.076]', 1.405, 0.000, 'software VASP, k-points mesh 12x12x12, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (12, 'T', 'NOVAMAG_theory_ICCRAM_Fe4Ge1_#166_1', 'AGA, crystal, energy, magnetization', 'Fe4Ge', 'obtained by Adaptive Genetic Algorithm Structure Prediction Methods,  software USPEX+VASP', 166, 59.0216, '[4.05323,4.05323,12.44511]', '[90,90,120]', '["Fe1(6c)",0,0,0.40229,"Fe2(6c)",0,0,0.20348,"Ge1(3a)",0,0,0]', 'software VASP, k-points mesh 12x12x12, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar,temperature=0K', -37.9776961, -0.455133, 'software VASP, k-points mesh 12x12x12, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 8.868000, '["1 Fe",2.118,"2 Fe",2.118,"3 Fe",2.362,"4 Fe",2.362,"5 Ge",-0.091]', 1.750, 0.000, 'software VASP, k-points mesh 12x12x12, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (13, 'T', 'NOVAMAG_theory_ICCRAM_Fe5Ge1_#164_1', 'AGA, crystal, energy, magnetization', 'Fe5Ge', 'obtained by Adaptive Genetic Algorithm Structure Prediction Methods,  software USPEX+VASP', 164, 70.0068, '[4.03646,4.03646,4.96145]', '[90,90,120]', '["Fe1(2d)",0.33333,0.66667,0.8241,"Fe2(2d)",0.33333,0.66667,0.33224,"Fe3(1b)",0,0,0.5,"Ge1(1a)",0,0,0]', 'software VASP, k-points mesh 11x11x8, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar,temperature=0K', -46.2484550, -0.468082, 'software VASP, k-points mesh 11x11x8, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 10.857000, '["1 Fe",1.923,"2 Fe",1.923,"3 Fe",2.482,"4 Fe",2.482,"5 Fe",2.15,"6 Ge",-0.102]', 1.806, 0.000, 'software VASP, k-points mesh 11x11x8, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (14, 'T', 'NOVAMAG_theory_ICCRAM_Fe5Ta1_#216_1', 'AGA, crystal, energy, magnetization', 'Fe5Ta', 'obtained by Adaptive Genetic Algorithm Structure Prediction Methods,  software USPEX+VASP', 216, 74.3592, '[6.67521,6.67521,6.67521]', '[90,90,90]', '["Fe1(16e)",0.37549,0.37549,0.37549,"Fe2(4d)",0.75,0.75,0.75,"Ta1(4a)",0,0,0]', 'software VASP, k-points mesh 10x10x10, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', -53.7371465, -0.635137, 'software VASP, k-points mesh 10x10x10, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 9.704000, '["1 Fe",1.919,"2 Fe",1.679,"3 Fe",1.905,"4 Fe",1.898,"5 Fe",2.74,"6 Ta",-0.437]', 1.520, 0.000, 'software VASP, k-points mesh 10x10x10, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (15, 'T', 'NOVAMAG_theory_ICCRAM_Fe6Ge1_#12_1', 'AGA, crystal, energy, magnetization', 'Fe6Ge', 'obtained by Adaptive Genetic Algorithm Structure Prediction Methods,  software USPEX+VASP', 12, 81.8752, '[8.58619,4.0383,4.74728]', '[90,95.84151,90]', '["Fe1(4i)",0.42714,0,0.70767,"Fe2(4i)",0.29041,0,0.14248,"Fe3(4i)",0.85607,0,0.43639,"Ge1(2a)",0,0,0]', 'software VASP, k-points mesh 8x11x11, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar,temperature=0K', -54.5482021, -0.510019, 'software VASP, k-points mesh 8x11x11, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 13.569000, '["1 Fe",2.193,"2 Fe",2.193,"3 Fe",2.318,"4 Fe",2.318,"5 Fe",2.328,"6 Fe",2.328,"7 Ge",-0.109]', 1.930, 0.000, 'software VASP, k-points mesh 8x11x11, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (16, 'T', 'NOVAMAG_theory_ICCRAM_Fe8Ta2_#166_1', 'AGA, crystal, energy, magnetization', 'Fe8Ta2', 'obtained by Adaptive Genetic Algorithm Structure Prediction Methods,  software USPEX+VASP', 166, 124.8423, '[4.13143,4.13143,25.33683]', '[90,90,120]', '["Fe1(3a)",0,0,0,"Fe2(3b)",0,0,0.5,"Fe3(6c)",0,0,0.10269,"Fe4(6c)",0,0,0.19872,"Fe5(6c)",0,0,0.69999,"Ta1(6c)",0,0,0.40059]', 'software VASP, k-points mesh 11x11x11, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', -90.5939054, -0.905506, 'software VASP, k-points mesh 11x11x11, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 15.053000, '["1 Fe",2.522,"2 Fe",1.785,"3 Fe",1.713,"4 Fe",1.715,"5 Fe",2.515,"6 Fe",2.513,"7 Fe",1.595,"8 Fe",1.597,"9 Ta",-0.45,"10 Ta",-0.451]', 1.404, 0.000, 'software VASP, k-points mesh 11x11x11, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (17, 'T', 'NOVAMAG_theory_ICCRAM_Fe10Ta2_#129_1', 'AGA, crystal, energy, magnetization', 'Fe10Ta2', 'obtained by Adaptive Genetic Algorithm Structure Prediction Methods,  software USPEX+VASP', 129, 147.5976, '[4.10079,4.10079,8.77694]', '[90,90,90]', '["Fe1(4f)",0.75,0.25,0.842,"Fe2(2c)",0.25,0.25,0.67371,"Fe3(2b)",0.75,0.25,0.5,"Fe4(2c)",0.25,0.25,-0.00171,"Ta1(2c)",0.25,0.25,0.33021]', 'software VASP, k-points mesh 10x10x5, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', -107.1897243, -0.985705, 'software VASP, k-points mesh 10x10x5, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 19.848000, '["1 Fe",1.897,"2 Fe",1.897,"3 Fe",1.946,"4 Fe",1.946,"5 Fe",2.427,"6 Fe",2.427,"7 Fe",1.599,"8 Fe",1.599,"9 Fe",2.526,"10 Fe",2.526,"11 Ta",-0.471,"12 Ta",-0.471]', 1.566, 0.000, 'software VASP, k-points mesh 10x10x5, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (18, 'T', 'NOVAMAG_theory_ICCRAM_Fe12Ge6_#164_1', 'AGA, crystal, energy, magnetization', 'Fe12Ge6', 'obtained by Adaptive Genetic Algorithm Structure Prediction Methods,  software USPEX+VASP', 164, 232.2461, '[5.06855,5.06855,10.43878]', '[90,90,120]', '["Fe1(6i)",0.49638,0.50362,0.69293,"Fe2(6i)",0.83243,0.16757,0.1005,"Ge1(2d)",0.33333,0.66667,0.4974,"Ge2(2c)",0,0,0.69875,"Ge3(2d)",0.33333,0.66667,0.10674]', 'software VASP, k-points mesh 9x9x4, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar,temperature=0K', -128.0384383, -1.996781, 'software VASP, k-points mesh 9x9x4, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 24.229000, '["1 Fe",2.018,"2 Fe",1.972,"3 Fe",1.855,"4 Fe",1.855,"5 Fe",2.018,"6 Fe",1.972,"7 Fe",2.257,"8 Fe",2.216,"9 Fe",2.167,"10 Fe",2.167,"11 Fe",2.257,"12 Fe",2.216,"13 Ge",-0.096,"14 Ge",-0.096,"15 Ge",-0.151,"16 Ge",-0.151,"17 Ge",-0.124,"18 Ge",-0.124]', 1.215, 0.000, 'software VASP, k-points mesh 9x9x4, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ge:PAW_PBE:05Jan2001, Pressure=0.0 kbar', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (19, 'T', 'NOVAMAG_theory_ICCRAM_Fe14Ta2_#139_1', 'AGA, crystal, energy, magnetization', 'Fe14Ta2', 'obtained by Adaptive Genetic Algorithm Structure Prediction Methods,  software USPEX+VASP', 139, 194.0480, '[4.08091,4.08091,11.65184]', '[90,90,90]', '["Fe1(8g)",0,0.5,0.87004,"Fe2(4e)",0,0,0.75117,"Fe3(2a)",0,0,0,"Ta1(2b)",0,0,0.5]', 'software VASP, k-points mesh 10x10x3, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', -140.3549987, -1.119738, 'software VASP, k-points mesh 10x10x3, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 30.010000, '["1 Fe",2.047,"2 Fe",2.047,"3 Fe",1.995,"4 Fe",1.995,"5 Fe",2.047,"6 Fe",2.047,"7 Fe",1.995,"8 Fe",1.995,"9 Fe",2.556,"10 Fe",2.556,"11 Fe",2.556,"12 Fe",2.556,"13 Fe",2.35,"14 Fe",2.35,"15 Ta",-0.542,"16 Ta",-0.542]', 1.801, 0.000, 'software VASP, k-points mesh 10x10x3, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (20, 'T', 'NOVAMAG_theory_ICCRAM_Fe16Ta4_#62_1', 'AGA, crystal, energy, magnetization', 'Fe16Ta4', 'obtained by Adaptive Genetic Algorithm Structure Prediction Methods,  software USPEX+VASP', 62, 246.2478, '[7.88434,4.80207,6.50397]', '[90,90,90]', '["Fe1(4c)",0.74176,0.25,0.0971,"Fe2(8d)",0.08377,-0.01188,0.6514,"Fe3(4c)",0.34127,0.25,0.75986,"Ta1(4c)",0.10622,0.25,0.03796]', 'software VASP, k-points mesh 5x8x6, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', -181.0968678, -1.720069, 'software VASP, k-points mesh 5x8x6, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 26.304000, '["1 Fe",1.872,"2 Fe",1.882,"3 Fe",1.872,"4 Fe",1.882,"5 Fe",1.697,"6 Fe",1.705,"7 Fe",1.674,"8 Fe",1.679,"9 Fe",1.697,"10 Fe",1.705,"11 Fe",1.674,"12 Fe",1.679,"13 Fe",1.698,"14 Fe",1.714,"15 Fe",1.698,"16 Fe",1.714,"17 Ta",-0.382,"18 Ta",-0.387,"19 Ta",-0.382,"20 Ta",-0.387]', 1.244, 0.000, 'software VASP, k-points mesh 5x8x6, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (21, 'T', 'NOVAMAG_theory_ICCRAM_Fe16Ta4_#129_1', 'AGA, crystal, energy, magnetization', 'Fe16Ta4', 'obtained by Adaptive Genetic Algorithm Structure Prediction Methods,  software USPEX+VASP', 129, 250.1375, '[4.08162,4.08162,15.01457]', '[90,90,90]', '["Fe1(4f)",0.75,0.25,0.70315,"Fe2(2b)",0.75,0.25,0.5,"Fe3(2c)",0.25,0.25,0.80559,"Fe4(2c)",0.25,0.25,0.60314,"Fe5(4f)",0.75,0.25,0.09324,"Fe6(2c)",0.25,0.25,-5.5E-4,"Ta1(2c)",0.25,0.25,0.40051,"Ta2(2c)",0.25,0.25,0.19375]', 'software VASP, k-points mesh 10x10x3, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', -181.2630756, -1.886277, 'software VASP, k-points mesh 10x10x3, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 30.337000, '["1 Fe",1.488,"2 Fe",1.488,"3 Fe",1.517,"4 Fe",1.517,"5 Fe",1.539,"6 Fe",1.539,"7 Fe",2.433,"8 Fe",2.433,"9 Fe",2.537,"10 Fe",2.537,"11 Fe",1.998,"12 Fe",1.998,"13 Fe",1.952,"14 Fe",1.952,"15 Fe",2.584,"16 Fe",2.584,"17 Ta",-0.396,"18 Ta",-0.396,"19 Ta",-0.484,"20 Ta",-0.484]', 1.413, 0.000, 'software VASP, k-points mesh 10x10x3, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO items VALUES (22, 'T', 'NOVAMAG_theory_ICCRAM_Fe20Ta4_#139_1', 'AGA, crystal, energy, magnetization', 'Fe20Ta4', 'obtained by Adaptive Genetic Algorithm Structure Prediction Methods,  software USPEX+VASP', 139, 296.0234, '[4.10895,4.10895,17.53333]', '[90,90,90]', '["Fe1(4e)",0,0,0.83631,"Fe2(4d)",0,0.5,0.25,"Fe3(2a)",0,0,0,"Fe4(8g)",0,0.5,-0.07925,"Fe5(2b)",0,0,0.5,"Ta1(4e)",0,0,0.33472]', 'software VASP, k-points mesh 10x10x2, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', -214.3846679, -1.976628, 'software VASP, k-points mesh 10x10x2, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar,temperature=0K', NULL, NULL, NULL, 40.333000, '["1 Fe",2.46,"2 Fe",2.46,"3 Fe",2.46,"4 Fe",2.46,"5 Fe",1.598,"6 Fe",1.598,"7 Fe",1.598,"8 Fe",1.598,"9 Fe",2.516,"10 Fe",2.516,"11 Fe",1.98,"12 Fe",1.98,"13 Fe",1.944,"14 Fe",1.944,"15 Fe",1.98,"16 Fe",1.98,"17 Fe",1.944,"18 Fe",1.944,"19 Fe",2.618,"20 Fe",2.618,"21 Ta",-0.466,"22 Ta",-0.466,"23 Ta",-0.466,"24 Ta",-0.466]', 1.587, 0.000, 'software VASP, k-points mesh 10x10x2, Ecut-off=410.533 eV, PAW_PBE, Fe_pv:PAW_PBE:02Aug2007, Ta_pv:PAW_PBE:07Sep2000, Pressure=0.0 kbar', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);


--
-- TOC entry 2361 (class 0 OID 38154)
-- Dependencies: 244
-- Data for Name: attached_files; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2358 (class 0 OID 38123)
-- Dependencies: 241
-- Data for Name: authors; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO authors VALUES ('ICCRAM');
INSERT INTO authors VALUES ('BCM');


--
-- TOC entry 2359 (class 0 OID 38128)
-- Dependencies: 242
-- Data for Name: authoring; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO authoring VALUES ('ICCRAM', 1);
INSERT INTO authoring VALUES ('ICCRAM', 2);
INSERT INTO authoring VALUES ('ICCRAM', 3);
INSERT INTO authoring VALUES ('BCM', 4);
INSERT INTO authoring VALUES ('BCM', 5);
INSERT INTO authoring VALUES ('BCM', 6);
INSERT INTO authoring VALUES ('BCM', 7);
INSERT INTO authoring VALUES ('BCM', 8);
INSERT INTO authoring VALUES ('BCM', 9);
INSERT INTO authoring VALUES ('BCM', 10);
INSERT INTO authoring VALUES ('ICCRAM', 11);
INSERT INTO authoring VALUES ('ICCRAM', 12);
INSERT INTO authoring VALUES ('ICCRAM', 13);
INSERT INTO authoring VALUES ('ICCRAM', 14);
INSERT INTO authoring VALUES ('ICCRAM', 15);
INSERT INTO authoring VALUES ('ICCRAM', 16);
INSERT INTO authoring VALUES ('ICCRAM', 17);
INSERT INTO authoring VALUES ('ICCRAM', 18);
INSERT INTO authoring VALUES ('ICCRAM', 19);
INSERT INTO authoring VALUES ('ICCRAM', 20);
INSERT INTO authoring VALUES ('ICCRAM', 21);
INSERT INTO authoring VALUES ('ICCRAM', 22);


--
-- TOC entry 2354 (class 0 OID 38061)
-- Dependencies: 237
-- Data for Name: composition; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO composition VALUES ('Fe', 'FeNi', 0.500);
INSERT INTO composition VALUES ('Ni', 'FeNi', 0.500);
INSERT INTO composition VALUES ('Al', 'AlMn', 0.500);
INSERT INTO composition VALUES ('Mn', 'AlMn', 0.500);
INSERT INTO composition VALUES ('Co', 'CoFe4Ta', 0.167);
INSERT INTO composition VALUES ('Fe', 'CoFe4Ta', 0.667);
INSERT INTO composition VALUES ('Ta', 'CoFe4Ta', 0.167);
INSERT INTO composition VALUES ('Fe', 'Fe3Sn', 0.750);
INSERT INTO composition VALUES ('Sn', 'Fe3Sn', 0.250);
INSERT INTO composition VALUES ('Fe', 'Fe9Mn3SbSn3', 0.563);
INSERT INTO composition VALUES ('Mn', 'Fe9Mn3SbSn3', 0.188);
INSERT INTO composition VALUES ('Sb', 'Fe9Mn3SbSn3', 0.063);
INSERT INTO composition VALUES ('Sn', 'Fe9Mn3SbSn3', 0.188);
INSERT INTO composition VALUES ('Fe', 'Fe8Mn4SbSn3', 0.500);
INSERT INTO composition VALUES ('Mn', 'Fe8Mn4SbSn3', 0.250);
INSERT INTO composition VALUES ('Sb', 'Fe8Mn4SbSn3', 0.063);
INSERT INTO composition VALUES ('Sn', 'Fe8Mn4SbSn3', 0.188);
INSERT INTO composition VALUES ('Fe', 'Fe6Mn6SbSn3', 0.375);
INSERT INTO composition VALUES ('Mn', 'Fe6Mn6SbSn3', 0.375);
INSERT INTO composition VALUES ('Sb', 'Fe6Mn6SbSn3', 0.063);
INSERT INTO composition VALUES ('Sn', 'Fe6Mn6SbSn3', 0.188);
INSERT INTO composition VALUES ('Fe', 'Fe15Mn15SbSn9', 0.375);
INSERT INTO composition VALUES ('Mn', 'Fe15Mn15SbSn9', 0.375);
INSERT INTO composition VALUES ('Sb', 'Fe15Mn15SbSn9', 0.025);
INSERT INTO composition VALUES ('Sn', 'Fe15Mn15SbSn9', 0.225);
INSERT INTO composition VALUES ('Fe', 'Fe3Ge', 0.750);
INSERT INTO composition VALUES ('Ge', 'Fe3Ge', 0.250);
INSERT INTO composition VALUES ('Fe', 'Fe4Ge', 0.800);
INSERT INTO composition VALUES ('Ge', 'Fe4Ge', 0.200);
INSERT INTO composition VALUES ('Fe', 'Fe5Ge', 0.833);
INSERT INTO composition VALUES ('Ge', 'Fe5Ge', 0.167);
INSERT INTO composition VALUES ('Fe', 'Fe5Ta', 0.833);
INSERT INTO composition VALUES ('Ta', 'Fe5Ta', 0.167);
INSERT INTO composition VALUES ('Fe', 'Fe6Ge', 0.857);
INSERT INTO composition VALUES ('Ge', 'Fe6Ge', 0.143);
INSERT INTO composition VALUES ('Fe', 'Fe8Ta2', 0.800);
INSERT INTO composition VALUES ('Ta', 'Fe8Ta2', 0.200);
INSERT INTO composition VALUES ('Fe', 'Fe10Ta2', 0.833);
INSERT INTO composition VALUES ('Ta', 'Fe10Ta2', 0.167);
INSERT INTO composition VALUES ('Fe', 'Fe12Ge6', 0.667);
INSERT INTO composition VALUES ('Ge', 'Fe12Ge6', 0.333);
INSERT INTO composition VALUES ('Fe', 'Fe14Ta2', 0.875);
INSERT INTO composition VALUES ('Ta', 'Fe14Ta2', 0.125);
INSERT INTO composition VALUES ('Fe', 'Fe16Ta4', 0.800);
INSERT INTO composition VALUES ('Ta', 'Fe16Ta4', 0.200);
INSERT INTO composition VALUES ('Fe', 'Fe20Ta4', 0.833);
INSERT INTO composition VALUES ('Ta', 'Fe20Ta4', 0.167);


--
-- TOC entry 2366 (class 0 OID 0)
-- Dependencies: 239
-- Name: items_mafid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('items_mafid_seq', 22, true);


-- Completed on 2017-11-01 20:58:46

--
-- PostgreSQL database dump complete
--

--DELETE OLD VERSIONS
/*
delete from items where name in (
'NOVAMAG_theory_ICCRAM_Fe12Ge6_#164_1', 
'NOVAMAG_theory_ICCRAM_Fe6Ge1_#12_1',
'NOVAMAG_theory_ICCRAM_Fe5Ge1_#164_1',
'NOVAMAG_theory_ICCRAM_Fe4Ge1_#166_1',
'NOVAMAG_theory_ICCRAM_Fe3Ge1_#225_1',

'NOVAMAG_theory_ICCRAM_Fe16Ta4_#129_1',
'NOVAMAG_theory_ICCRAM_Fe16Ta4_#62_1',
'NOVAMAG_theory_ICCRAM_Fe14Ta2_#139_1',
'NOVAMAG_theory_ICCRAM_Fe10Ta2_#129_1',
'NOVAMAG_theory_ICCRAM_Fe20Ta4_#139_1',
'NOVAMAG_theory_ICCRAM_Fe8Ta2_#166_1',
'NOVAMAG_theory_ICCRAM_Fe5Ta1_#216_1');
*/
