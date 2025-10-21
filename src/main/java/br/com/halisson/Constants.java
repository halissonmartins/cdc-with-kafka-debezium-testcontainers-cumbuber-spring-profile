package br.com.halisson;

public class Constants {	
	
	public static final String TO_TOPIC_NAME = "to-replication-customer";
	public static final String FROM_TOPIC_NAME = "from.testcontainers.customers";
	
	public static final String JSON_MESSAGE_TEMPLATE = """
			{
			  "schema": {
			    "type": "struct",
			    "fields": [
			      {"type": "int64", "optional": false, "field": "id"},
			      {"type": "string", "optional": false, "field": "name"},
			      {"type": "string", "optional": false, "field": "email"}
			    ],
			    "optional": false,
			    "name": "testcontainers.customers"
			  },
              "payload": {
			    "id": %d,
			    "name": "%s",
			    "email": "%s"
			  }
			}
			""";

}
