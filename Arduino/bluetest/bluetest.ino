
#include <Servo.h>
#include <SoftwareSerial.h>
int bluetoothTx = 2;
int bluetoothRx = 3;
Servo servo;
int value=90;
long toSend;
int k=0;
int lk=0;
int rk=0;
SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);
 
void setup()
{
  servo.attach(9);
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
    
    if(toSend==13||toSend==18||toSend==19) {//right
      value=0;
      servo.write(value);
      delay(7000);
      servo.write(90);
    }
    else if(toSend==213||toSend==216||toSend==217){//오른쪽 횡단보도
       while(rk<2){
         for(value=0;value<90;value++){
          servo.write(value);
          delay(10);
         }
         rk++;    
       }
       servo.write(90);
      } 
    else if (toSend==12||toSend==16||toSend==17) { //left
      value=180;
      servo.write(value);
      delay(7000);
      servo.write(90);
    }
    else if(toSend==212||toSend==214||toSend==215) {//왼쪽 횡단보도
       while(lk<2){
         for(value=180;value>90;value--){
          servo.write(value);
          delay(10);
         }
         lk++;    
       }
       servo.write(90);
    }
    else if(toSend==11) {//직진
      value=90;
      servo.write(value);
      delay(7000);
      servo.write(90);
    }
    else if(toSend==211){//정면 횡단보도
        for(value=60;value<120;value++){
         servo.write(value);
         delay(10);
        }
        for(value=120;value>60;value--){
         servo.write(value);
         delay(10);
        }    
        servo.write(90);
     }
    else if(toSend==201){//도착
      while(k<2){
          for(value=90;value<180;value++){
            servo.write(value);
            delay(15);
            }
          for(value=180;value>0;value--){
            servo.write(value);
            delay(15);
            }
          for(value=0;value<90;value++){
            servo.write(value);
            delay(15);
            }
            k++;
          }
    }
      
    Serial.println(toSend);
    
  }
  
  
}
