
#include <Servo.h>
#include <SoftwareSerial.h>
int bluetoothTx = 2;
int bluetoothRx = 3;
Servo servo;
int value=90;
long toSend;
SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);
 
void setup()
{
  servo.attach(4);
  servo.write(value);
  //Setup usb serial connection to computer
  Serial.begin(9600);
  bluetooth.begin(9600);
}
 
 
void loop()
{ 
  //Read from bluetooth and write to usb serial 
  if(bluetooth.available())
  {
    toSend = 0;
    toSend = bluetooth.parseInt();
    
    if(toSend==13||toSend==18||toSend==19||toSend==213||toSend==216||toSend==217) {
      value=0;
      servo.write(value);
      delay(7000);
      servo.write(90);
    } else if (toSend==12||toSend==16||toSend==17||toSend==212||toSend==214||toSend==215) { //left
      value=180;
      servo.write(value);
      delay(7000);
      servo.write(90);
    } else if(toSend==11) {
      value=90;
      servo.write(value);
      delay(7000);
      servo.write(90);
    }
      
    Serial.println(toSend);
    
  }
  
  
}
