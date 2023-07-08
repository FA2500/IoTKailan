#include <Servo.h>
#include "DHT.h"
#include <ArduinoJson.h>

#define DHTTYPE DHT11  
Servo myservo;
int motor=7;
int pos=0;
bool isRaining=false;
bool tooHumid=false;

//Plant 1 (MOIST / 12)
#define DHTPIN 13          //HUMIDITY & TEMPERATURE
int moisturePIN=A0;
DHT dht(DHTPIN, DHTTYPE);
int sensorpin=A1;   
int outputpin=12;
int waterpump1=11;
int water;
int waterlevelpin=8;
int waterlevel;

//Plant 2 (LACKS / 01)
#define DHTPIN2 2
DHT dht2(DHTPIN2, DHTTYPE);
int sensorpin2=A5;   
int outputpin2=4; 
int waterpump2=10;
int water2;

//struct class
struct KeyValuePair {
  String key;
  int value;
};
//struct class

void setup() {
  Serial.begin(9600);
  pinMode(sensorpin,INPUT);    //setting sensor pin to be input
  pinMode(outputpin,INPUT);   //setting output pin to be output
  pinMode(sensorpin2,INPUT);    //setting sensor pin to be input
  pinMode(outputpin2,INPUT);   //setting output pin to be output
  pinMode(motor,OUTPUT);
  pinMode(waterpump1,OUTPUT);
  pinMode(waterpump2,OUTPUT);
  pinMode(waterlevelpin,OUTPUT);
  digitalWrite(waterlevelpin, HIGH);
  myservo.attach(6);
  digitalWrite(motor,LOW); //HIGH BARU FUNCTION
  dht.begin();
  dht2.begin();
}

void loop() {
  digitalWrite(waterpump1,HIGH); //LOW BARU FUNCTION
  digitalWrite(waterpump2,HIGH);
  delay(100);
  float temperature = dht.readTemperature();
  delay(100);
  float humidity = dht.readHumidity();
  delay(100);
  float temperature2 = dht2.readTemperature();
  delay(100);
  float humidity2 = dht2.readHumidity();
  delay(100);
  int sensorValue = analogRead(moisturePIN);
  delay(100);
  //water=analogRead(sensorpin);
  water=digitalRead(outputpin);  
  delay(100);
  //water2=analogRead(sensorpin2);
  water2=digitalRead(outputpin2);
  delay(100);
  waterlevel=digitalRead(waterlevelpin);
  delay(100);


  /*if(isRaining==false && (water < 500 && water2 < 500))
  {
    isRaining=true;
    for (pos = 0; pos <= 90; pos += 1) { // goes from 0 degrees to 180 degrees
    // in steps of 1 degree
    myservo.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
    }
  }
  else if(isRaining==true&&(water>=500 && water2>=500))
  {
    isRaining=false;
    for (pos = 90; pos >= 0; pos -= 1) { // goes from 180 degrees to 0 degrees
    myservo.write(pos);              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
    }
  }
  if(tooHumid==false&&(humidity>80 && humidity2>80))
  {
    tooHumid=true;
    digitalWrite(motor,HIGH);

  }
  else if(tooHumid==true&&(humidity<=80 && humidity2<=80))
  {
    tooHumid=false;
    digitalWrite(motor,LOW);
  }
  if(sensorValue>=500)
  {
    digitalWrite(waterpump1,LOW);
    delay(10000);
  }
  else
  {
    digitalWrite(waterpump1,HIGH);
  }*/

  KeyValuePair dictionary[] = {
  {"temperature1", temperature},
  {"temperature2", temperature2},
  {"humidity1", humidity},
  {"humidity2", humidity2},
  {"water1", water},
  {"water2", water2},
  {"moisture", sensorValue},
  {"waterlevel", waterlevel},
};

 String output;
  for (int i = 0; i < sizeof(dictionary) / sizeof(dictionary[0]); i++) {
    if(i==0)
    {
      output += "{";
    }
    output += "\"" + dictionary[i].key + "\": \"" + String(dictionary[i].value) + "\"";
    if(i!=(sizeof(dictionary)/sizeof(dictionary[0])-1))
    {
      output += ", ";
    }
    if(i==(sizeof(dictionary)/sizeof(dictionary[0])-1))
    {
      output += "}";
    }
  }
  if(Serial.available() > 0)
  {
    String teststr = Serial.readString();
    teststr.trim();
    teststr=teststr.substring(0,5);
    //Serial.println(teststr);
    if(teststr=="Ready")
    {
      Serial.println(output);
    }
    else if(teststr=="pump1")
    {
      waterpump1exe();
    }
    else if(teststr=="pump2")
    {
      waterpump2exe();
    }
    else if(teststr=="fanon")
    {
      fan1exe();
    }
    else if(teststr=="servo")
    {
      servoexe();
    }

  }
  delay(3000);

  /*Serial.print(temperature);
  Serial.print(" ");
  Serial.print(temperature2);
  Serial.print(" ");
  Serial.print(humidity);
  Serial.print(" ");
  Serial.print(humidity2);
  Serial.print(" ");
  Serial.print(water);
  Serial.print(" ");
  Serial.print(water2);
  Serial.print(" ");
  Serial.print(sensorValue);
  Serial.print(" ");
  Serial.print(waterlevel);
  Serial.print(" ");
  Serial.print("END");
  Serial.println(" ");
  //Serial.flush();*/

  // Wait a few seconds between measurements.
  //delay(4200);
}

void waterpump1exe()
{
  digitalWrite(motor,HIGH);
  digitalWrite(waterpump1,LOW);
  delay(5000);
  digitalWrite(waterpump1,HIGH);
}

void waterpump2exe()
{
  digitalWrite(motor,HIGH);
  digitalWrite(waterpump2,LOW);
  delay(5000);
  digitalWrite(waterpump2,HIGH);
}

void fan1exe()
{
  digitalWrite(motor,LOW);
  delay(10000);
  digitalWrite(motor,HIGH);
}

void servoexe()
{

}






/*#include <Servo.h>
Servo servo;
int angle = 10;
void setup() {
  servo.attach(8);
  servo.write(angle);
}
void loop() 
{ 
 // scan from 0 to 180 degrees
  for(angle = 10; angle < 180; angle++)  
  {                                  
    servo.write(angle);               
    delay(15);                   
  } 
  // now scan back from 180 to 0 degrees
  for(angle = 180; angle > 10; angle--)    
  {                                
    servo.write(angle);           
    delay(15);       
  } 
}*/
