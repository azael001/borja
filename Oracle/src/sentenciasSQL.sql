insert into Alumnos VALUES(?,?,?,?,?)
select Alumnos.nombre, Alumnos.telefono,Alumnos.Cod_pincel from Alumnos inner join Ciclos ON Alumnos.Cod_pincel=Ciclos.Cod_pincel where Ciclos.grado='S' order by Alumnos.nombre desc
select Cod_pincel ,count(Cod_pincel) as Numero_Alumnos_Matriculados from Alumnos group by Cod_pincel
update Ciclos set Modalidad = 'S' where Modalidad = 'D'