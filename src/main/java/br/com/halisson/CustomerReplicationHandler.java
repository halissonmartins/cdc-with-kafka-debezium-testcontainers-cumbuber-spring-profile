package br.com.halisson;

import java.util.LinkedHashMap;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerReplicationHandler {
	
	private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = Constants.FROM_TOPIC_NAME, groupId = "replication-customer-group")
    public void receive(ConsumerRecord<String, String> consumerRecord) {

    	String value = consumerRecord.value();
        log.info("received payload={}", value);
        
//		String data = transform(value); 
//		log.info("Sending data={}", data);
        
		kafkaTemplate.send(Constants.TO_TOPIC_NAME, value);
    }

	private String transform(String value) {
        
//        String schema = JsonPath.<String>read(value, "$.source.schema");
//        log.info("received payload schema={}", schema);
//        String table = JsonPath.<String>read(value, "$.source.table");
//        log.info("received payload table={}", table);
        LinkedHashMap<String, Object> afterLinkedHashMap = JsonPath.<LinkedHashMap<String, Object>>read(value, "$.after");
        log.info("received payload afterJson={}", afterLinkedHashMap);
        
        Integer id = (Integer) afterLinkedHashMap.get("id");//TODO it's a problema if return a LONG		
		String name = (String) afterLinkedHashMap.get("name");
		String email = (String) afterLinkedHashMap.get("email");
		
		log.info("received payload after={}_{}_{}", id, name, email);
        
		return String.format(Constants.JSON_MESSAGE_TEMPLATE, id, name, email);
        
	}

}
