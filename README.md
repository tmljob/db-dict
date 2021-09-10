# 使用说明

数据库表结构文档生成工具，基于开源的[screw](https://github.com/pingfangushi/screw)封装，扩展支持了excel文档类型，使用说明如下：



1.在application.properties配置需要连接的数据库的相关信息。

```
#数据库配置
spring.datasource.url=jdbc:mysql://10.19.248.222:3306/enc_vehicle
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=Bocom_123
```

生成的数据库字段文档，支持HTML/WORD/EXCEL/MD格式，配置如下。

```
#文档信息配置，支持HTML/WORD/MD/EXCEL
dic.file.type=EXCEL
dic.file.version=1.0.1
dic.file.discription=数据库设计文档生成
dic.file.name=危化品园区管控-数据库设计文档
```



2、运行start.bat脚本，运行完毕后即可生成数据库字典文档。

​      运行完毕后，文档默认生成在脚本同级的/doc/目录下面，程序默认会自动打开该目录。