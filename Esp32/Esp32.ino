#include <WiFi.h>;
#include <HTTPClient.h>;
#include <ArduinoJson.h>;
#include "DHT.h"
#define DHTPIN 4
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);
const char* ssid="Saloni"; 
const char* password="Saloni123@";
char jsonOutput[128];
void setup() {
  Serial.begin(9600);
  WiFi.begin(ssid,password);
  Serial.print("Connecting to wifi ");
  while(WiFi.status()!=WL_CONNECTED)
  {
    Serial.print(".");
    delay(500);
  }
  Serial.println("\nConnected to wifi");
  Serial.print("IP adress : ");
  Serial.print(WiFi.localIP());
  Serial.println("DHT11 sensor!");
  dht.begin();
}

void loop() {
  if((WiFi.status()==WL_CONNECTED))
  {
    float h = dht.readHumidity();
    float t = dht.readTemperature();
    if (isnan(h) || isnan(t)) {
    Serial.println("Failed to read from DHT sensor!");
    return;
    }
    HTTPClient client;
    client.begin("http://192.168.1.11:1111/data/1");
    client.addHeader("Content-Type","application/json");
    const size_t CAPACITY = JSON_OBJECT_SIZE(2);
    StaticJsonDocument<CAPACITY> doc;
    JsonObject obj=doc.to<JsonObject>();
    obj["humidity"]=h;
    obj["temperature"]=t;
    serializeJson(doc,jsonOutput);
    int httpCode =client.PUT(String(jsonOutput));
    if(httpCode>0)
    {
      String payload=client.getString();
      Serial.println("\nStatuscode "+String(httpCode));
      Serial.println(payload);
      client.end();
    }
    else
    {
      Serial.print("Error on server site");
    }
  }
  else
  {
    Serial.print("Connection Lost");
  }
  delay(1000);
}
