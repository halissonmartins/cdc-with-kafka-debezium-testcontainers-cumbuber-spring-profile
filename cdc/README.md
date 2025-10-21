## Como Executar o kafka consumer

- Executar os seguintes comandos no console do container kafka:

* Dentro do container do Kafka executar os seguintes comandos para criar consumidores de teste e visualizar os eventos:
```
cd opt/bitnami/kafka/bin

kafka-topics.sh --bootstrap-server=localhost:9092 --list

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic from-replication-customer-topic --from-beginning

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic to-replication-customer-topic --from-beginning

```

- Comando para conferir se o container do kafka-connect está ON ou se o conector foi adicionado com sucesso:
```
curl -X GET  -H  "Content-Type:application/json" http://localhost:8083/connectors
```

- Comandos para adicionar os conectores do postgres no container do debezium-connect:
```
curl -X POST  -H  "Content-Type:application/json" http://localhost:8083/connectors -d @from_source.json


curl -X POST  -H  "Content-Type:application/json" http://localhost:8083/connectors -d @to_target.json
```

- Script para testar se o CDC está funcionando corretamente:
```
insert into testcontainers.customers (name, email) values 
	('Jane', 'jane.smith@mail.com'); 

-----------------------------

UPDATE testcontainers.customers
SET email='jane@mail.com'
WHERE id=3;

```