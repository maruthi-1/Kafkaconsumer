package flink

final case class Empdata(empno:String, ename:String,designation:String,manager:String,hire_date:String,sal:String,deptno:String,
                         timeStamp : Long)
final case class Messages(key : String,
                          value : String)