# Hector-Smells
"Au!" -Michael Jackson
"Oi!" - tradução livre




Requisitos:
-java
-biblioteca general_midi (javaSound)
-saída de som (speaker)
-entrada de texto (arquivo.txt)
-ver se biblioteca traz ritmo //check



Funcionalidade:
-sem canivetes suíços 👍
-função que abre arquivo
 Recebe o caminho para o arquivo
 Retorna ponteiro para a estrutura de dados do arquivo 


-função que lê e percorre arquivo
 Recebe ponteiro para estrutura de dados do arquivo
 Enquanto não chegar ao final do arquivo:
  Retorna o caracter atual e incrementa seu ponteiro

-função decodifica o caracter e direciona pra uma das funções de mudança (é um switch case)(provavelmente vai chamar outras funções com switch case que então farão o direcionamento)
 Recebe o caracter atual
 Possui todas as comparações necessárias
 Direciona para funções de:
  Salva semitom em Note[] song
  Mudar instrumento
  Mudar oitava
  Mudar volume


Juntas essas funções convertem o texto do txt em vetores com todas as propriedades e mudanças da música a ser tocada 

inicia com volume, oitava e instrumentos default.

interface com usuário:
  Carregar txt
  Começar musica
  Pausar
  Reiniciar
  Mudar oitava
  Mudar instrumento
  Mudar volume
   





-no hector smells


Nota:
-Oitava
-Nota
-Nota anterior
-pausa é nota

classe Sound:
  int volume;
  int instrument;
  int octave;
  set volume;
  set instrument;
  set octave;
  
classe Note:
  int semitone;
  int duration; //fixa
  set semitone;
  set duration;

classe song:
  Track track;
  int bpm;
  void add(Sound, Note);


Funções de saída:
-tocam música

Funções de mudança:
-set octave;
-set instrument
-set volume;
-set semitone;
-set duration;

Interface gráfica:
-Bonita
-Poder iniciar, pausar e recomeçar a música
-*fazer alterações (volume, tom, instrumento)
