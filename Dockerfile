FROM hametan/datax-web:2.1.2

ADD ./datax-admin/target/datax-admin-2.1.2.jar /opt/
ADD ./datax-executor/target/datax-executor-2.1.2.jar /opt/

