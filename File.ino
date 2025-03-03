int trigPin;
int echoPin;
int[6] a;

void begin() {
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
}
int getDistance() {
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  duration = pulseIn(echoPin, HIGH);
  distance = (duration*.0343)/2;
  return((int) distance);
}
