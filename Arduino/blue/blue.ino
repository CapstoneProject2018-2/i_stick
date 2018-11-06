#include <SoftwareSerial.h>
#include <Servo.h>
SoftwareSerial softwareSerial(2, 3); // RX, TX
Servo servo;
int value=90;


void setup() {
  servo.attach(4);
  softwareSerial.begin(9600);
  servo.write(value);
}

void loop() { 

  if (softwareSerial.available() > 0 ) {
    


    char command = softwareSerial.read();
    if(command == '1')
    value=0;
    else if(command =='2')
    value=180;
    else if(command=='3')
    value=90;
   
    servo.write(value);
  }
}
