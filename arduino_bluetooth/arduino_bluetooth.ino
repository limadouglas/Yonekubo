
#include <SoftwareSerial.h>       // necessario importar a biblioteca SoftwareSerial, pois ela faz o gerenciamento de entrada e saida serial do arduino.
 
SoftwareSerial serial(8, 9);      // Definição de um objeto SoftwareSerial, pinos 8 e 9, como RX e TX, respectivamente.

String data;                      // A String data será utilizada para armazenar dados vindos do Android. 
int led = 2;                      // O led conectado ao pino 2.
 
void setup() {
  serial.begin(9600);
  pinMode(led, OUTPUT);           // led em modo de saida.
}
 

void loop() {

  // No início de cada loop, é verificado se há algo no buffer
  // Se houver bytes disponíveis, significa 
  // que o Android enviou algo, então é feita a leitura do novo caractere.
 
  while(serial.available() > 0)   // verificando se algo foi recebido.
    data = char(serial.read());   // salvando dado recebida na String data.
  
  // EXEMPLO DE LEITURA DO DADO RECEBIDO.
  
  // ligar
  if(data == "a")
    digitalWrite(led, HIGH);
  

  // desligar
  if(data == "b")
    digitalWrite(led, LOW);
  
  // piscar
  if(data == "p") { 
      digitalWrite(led, HIGH);
      delay(500);
      digitalWrite(led, LOW);
      delay(500);
  }

  
}
