from kafka import KafkaProducer
import json

topic = 'SAVE_ORDER_TOPIC'
host = 'localhost:9092'
producer = KafkaProducer(
  bootstrap_servers=[host],
  value_serializer=lambda x: json.dumps(x).encode('utf-8')
)

with open('orders-events.json') as file:
  datas = json.load(file)


for data in datas:
  print(data)
  producer.send(topic, value=data)
  producer.flush()

producer.close()