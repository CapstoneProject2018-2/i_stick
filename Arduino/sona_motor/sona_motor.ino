
int trigPin = 9;
int echoPin = 8;
int motorPin = 6;
void setup(){
  Serial.begin(9600);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
}
 

void loop(){
  int d;
   digitalWrite(trigPin, HIGH);
   delayMicroseconds(10); 
   digitalWrite(trigPin, LOW); 
   d = pulseIn(echoPin, HIGH)/58.2; /* 센치미터(cm) */
 
  if(d<100){
  Serial.print("DIstance:");
  Serial.print(d);
  Serial.println("cm\n");
  analogWrite( motorPin , 100 );
  delay(500);
  }
}
