
int trigPin = 9;
int echoPin = 8;
int motorPin = 13;
void setup(){
  Serial.begin(9600);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
}
 

void loop(){
  float dis;
  unsigned long dur;
   digitalWrite(trigPin, HIGH);
   delayMicroseconds(10); 
   digitalWrite(trigPin, LOW); 

   dur = pulseIn(echoPin , HIGH);
   dis = dur/29/2;

  if(dur<8000){
  Serial.print(dur);
  Serial.println("\n");
  analogWrite( motorPin , 200 );
  delay(500);
  }
  analogWrite(motorPin, 0);
 }  
