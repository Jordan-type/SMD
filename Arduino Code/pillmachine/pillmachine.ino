String bluetoothRead, Str_x;
int x;
int length;
#include "Servo.h"
Servo motor2; // create a servo object to control a servo
Servo motor1;
Servo motor3;
Servo motor4;
int mspeed = 1500; // variable to store the motor speed
int mmspeed;
 
void setup() {
  
  Serial.begin(9600); 
  // attaches the servo to the servo object amotor
  motor1.attach(11);
  motor2.attach(9);
  motor3.attach(6);
  motor4.attach(5);
  
  mspeed = 1480; //set position variable
  motor1.writeMicroseconds(mspeed); // tell servo to move as indicated by variable 'mspeed'

  mspeed = 1500; //set position variable
  motor2.writeMicroseconds(mspeed); // tell servo to move as indicated by variable 'mspeed'
  mmspeed = 88; //set position variable
 motor3.write(mmspeed);
  mmspeed = 90; //set position variable
  motor4.write(mmspeed); // tell servo to move as indicated by variable 'mspeed'

  delay(5000);
  while (!Serial);

}

void loop() {
  int i=0;
  char commandbuffer[200];
  Str_x="";

  if(Serial.available())
  {
     delay(10);
     while( Serial.available() && i< 199) 
     {
        commandbuffer[i++] = Serial.read();
     }      
  }
     commandbuffer[i++]='\0';
     
     bluetoothRead = (char*)commandbuffer;
     if(bluetoothRead.substring(0,1).equals("c"))
     {
       Str_x = bluetoothRead.substring(1, 2);      
     }
     
     x = Str_x.toInt();
     switch (x) {
        case 1:
          startmotor1();
          break;
        case 2:
          startmotor2();
          break;
        case 3:
          startmotor3();
          break;
        case 4:
          startmotor4();
          break;
        default: 
          // statements
        break;
      }   

}

void startmotor1()
{
    mspeed = 1600; //set position variable
    motor1.writeMicroseconds(mspeed); // tell servo to move as indicated by variable 'mspeed'
    delay(150); //time for the servo to move
    Serial.println("hi");
    //Stop Motor
    mspeed = 1480; //set position variable
    motor1.writeMicroseconds(mspeed); // tell servo to move as indicated by variable 'mspeed'

}

void startmotor2()
{   
    mspeed = 1700; //set position variable
    motor2.writeMicroseconds(mspeed); // tell servo to move as indicated by variable 'mspeed'
    delay(90); //time for the servo to move    
    //Stop Motor
    mspeed = 1500; //set position variable
    motor2.writeMicroseconds(mspeed);
  return;
}
void startmotor3()
{
  // Run Motor
    mmspeed = 95; //set position variable
    motor3.write(mmspeed); // tell servo to move as indicated by variable 'mspeed'
    delay(200); //time for the servo to move
    
    //Stop Motor
    mmspeed = 88; //set position variable
    motor3.write(mmspeed); // tell servo to move as indicated by variable 'mspeed'

}
void startmotor4()
{
    mmspeed = 98; //set position variable
    motor4.write(mmspeed); // tell servo to move as indicated by variable 'mspeed'
    delay(230); //time for the servo to move
   
    //Stop Motor
    mmspeed = 90; //set position variable
    motor4.write(mmspeed); // tell servo to move as indicated by variable 'mspeed'

}


