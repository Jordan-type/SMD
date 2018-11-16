Smart Medicine Dispenser
https://ieeexplore.ieee.org/document/8402399/

we built an Android application that is responsible of controlling
the whole system.
It’s the primary way of interacting with the system, the
application stores its data on the cloud and performs
synchronization upon login. To dispense the pills, the phone
will automatically connect to the Arduino via Bluetooth and
starts sending commands indicating which container and
Stepper Motor should be rotated.


The whole system relies on the android application to
provide the user interface, control the medicine dispenser and
manage user schedule and usage data

When the application starts it shows a login screen where
the user is authenticated ( needs more work)

After the user login, the application will show an
overview of the pills to be taken on the same day and on the
next day in another Tab, it will also provide a History Tab that
shows when old pills were taken

To add a new pill alarm the user should press on the Plus
icon and then the pill’s name and time he should also specify
on what days the alarm should be repeated. The user should
also choose in what container (from the connected Pillbox) the
pill is placed.

The application also provides an overview of existing
pills with their respective remaining number of pills. The User
can select a pill timer to change it or delete it. Or if he/she
sees that the remaining pill supply is low he/she can click on
the refill button the increase the remaining
pills back to 8.

Included in the application is a settings tab where the user
can enter his/her Caretaker’s phone number and add new
username and password.
When it is time to take the pill, an alarm sound will start
and will not stop till the user selects an option of these 3:
- Take the pill now: which will then dispense the pill in
its respective container, and decrease the number of pills
remaining
- Snooze the pill for 10 minutes;
- Or Select to not take the pill, where the application
will then alert the number, saved in the settings, that the
patient skipped his/her pill via SMS.


The pill alarms and usage data are stored first locally using
SQLite Database. The local database is then synced with an
online MySQL Database, hosted on the 000webhost.com
servers for free, whenever the user enters the application or
changes something in the schedule using PHP and JSON as a
way to communicate and transfer data between databases.

The SMD prototype, has expandable container units. Each container
is controlled separately with its own LED and can keep up to 7
servings (a serving can consist of multiple pills of the same type).
Servo motors are used to rotate the cylinders; the motors are
controlled by an Arduino Uno R3, using PWM signals that
make the servo rotate for a bit then stop, and are connected as
shown in Figure 11. When the user wants to take his pills, his
smartphone will connect to the Arduino via Bluetooth, and
sends it which container should be rotated.

The Arduino will then verify if the command is valid by
checking if the command string starts with a “c”, the character
that comes after the “c” is the container number which will be
used to trigger the desired container as shown in Figure 12.