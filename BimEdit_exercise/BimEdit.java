/*
 * Harjoitustyö.
 *
 * Lausekielinen ohjelmointi II.
 *
 * Ohjelma käsittelee kuvia käyttäjän antamien komentojen mukaan.
 *
 * Maija Hakonen.
 *
 */

// Scanner käyttöön.
import java.util.Scanner;
// Otetaan käyttöön tiedostonkäsittelyyn liittyviä palveluja.
import java.io.*;

/*
 * Ohjelma käsittelee ASCII-grafiikkakuvia käyttäjän antamien komentojen mukaan.
 * Tarjolla olevat komennot ovat print, invert, info, load, dilate, erode ja quit.
 * Ohjelma tarkistaa syötetyn kuvan ja komentojen oikeellisuuden.
 *
 */

public class BimEdit {

// Liitetään oletussyötevirtaan syötteiden lukija luokkavakiona.
public static final Scanner LUKIJA = new Scanner(System.in);

// Luodaan ohjelmassa usein käytettäviä luokkavakiota.
// Luokkavakiot tulostamiseen, kuvan värien vaihtoon, kuvatietojen tulostamiseen,
// kuvan lataamiseen, kuvan laajentamiseen, kuvan kutistamiseen ja ohjelman 
// lopettamiseen.
public static final String TULOSTA = "print";
public static final String VARIT = "invert";
public static final String TIEDOT = "info";

public static final String LATAA = "load";
public static final String KASVATA = "dilate";
public static final String KUTISTA = "erode";
public static final String LOPETA = "quit";


   /*
   * Metodi tarkistaa parametrina saatujen arvojen oikeellisuuden.
   * Palauttaa kelvon syötteen saatuaan true, viallisen kohdalla false
   * tutkittava = Käyttäjän antamat tutkittavat syötteet.
   */
   public static boolean tutkiKomento(String[] tutkittava) {
      if (tutkittava != null && tutkittava.length > 0) {
      
         // Tarkistetaan ensin, onko nimiosa oikein. Palautetaan false, jos ei.
         String ekaAlkio = tutkittava[0];
         String loppu = ekaAlkio.substring(ekaAlkio.length() - 4);
         if (!loppu.equals(".txt")) {
            System.out.println("Invalid command-line argument!");
            return false;
         }

         if (tutkittava.length == 2 && tutkittava[1] != "echo") {
            System.out.println("Invalid command-line argument!");
            return false;
         }
         
         if (tutkittava.length > 2) {
            System.out.println("Invalid command-line argument!");
            return false;
         }
                    
         return true;
      }
      System.out.println("Invalid command-line argument!");
      return false;
   }

   
   /*
    * Metodi luo kaksiulotteisen char-tyyppisten alkioiden taulukon ja
    * lataa annetun tekstitiedoston merkit taulukkoon niin, että eka merkki on kohdassa (0,0)
    * ja viimeinen merkki kohdassa (n - 1, m - 1).
    * tiedostonNimi = Alkuperäinen tiedosto.
    * perusTiedot = (Vielä) tyhjä taulukko, joka tulee sisältämään seuraavat alkiot: riviLkm, sarakeLkm,
    *                 taustamerkki, edustamerkki.
   */
   public static char[][] lataaKuva(String tiedostonNimi, String[] perusTiedot) {
      // Tarkistetaan syötteet, palautetaan tyhjillä arvoilla null.
      if (tiedostonNimi == null || perusTiedot == null) {
         return null;
      }
      
      // Esitellään ulkona try-lohkosta, jotta voidaan käyttää catch-lohkossa.
      Scanner tiedostonLukija = null;

      // Luodaan huviksemme apumuuttuja (eiku oikeesti tätä tarvitaan).
      int apu = 0;

      try {
         // Luodaan tiedostoon liittyvä olio.
         File tiedosto = new File(tiedostonNimi);

         // Liitetään lukija tiedostoon.
         tiedostonLukija = new Scanner(tiedosto);

         // Käydään tuolla, täytetään perusTiedot. tarkistetaam onko kuva oikean kokoinen.
         // False, jos ilmoitettu virheellinen kuvan koko (alle 3 x 3 alkiota).
         boolean pupuTiedot = tietoKaivaja(tiedostonLukija, perusTiedot);
         
         // Tallennetaan rivimäärä sarakemäärä, tausta- ja edustamerkit muuttujiin.
         int riviLkm = Integer.parseInt(perusTiedot[0]);
         int sarakeLkm = Integer.parseInt(perusTiedot[1]);
         char tausta = perusTiedot[2].charAt(0);
         char edusta = perusTiedot[3].charAt(0);
         
         if (pupuTiedot == false) {
            return null;
         }
         
         // Luodaan uusi, vielä tyhjä kuvatiedosto.
         char[][] kuvake = new char[riviLkm][sarakeLkm];
         
         // Ladataan annettu kuva tiedostoon. Käydään kuvaa läpi kunnes seuraavaa riviä ei 
         // enää ole.
         while (tiedostonLukija.hasNextLine()) {

            // Luetaan tiedoston rivi.
            String rivi = tiedostonLukija.nextLine();
            int riviPituus = rivi.length();
            
            // Jos rivi ei ole oikean mittainen (=alkioita ei tarpeeksi), palautetaan null.
            if (sarakeLkm != riviPituus) {
               return null;
            }

            // Käydään rivi läpi ja sijoitetaan sen alkiot kuvatiedostoon riville i.
            // Jos rivejä on enemmän kuin ilmoitettu, kuvake[apu][i] heittää poikkeuksen.
            for (int i = 0; i < sarakeLkm; i++) {
               kuvake[apu][i] = rivi.charAt(i);
                  // Jos sijoitettava merkki ei vastaa annettuja, palautetaan null.
                  if ((kuvake[apu][i] != tausta) && (kuvake[apu][i] != edusta)) {
                     return null;
                  }
            }
            apu++;
         }
         
         if (apu != riviLkm) {
            return null;
         }
         
         // Jos läpikäytyjen rivien määrä ei vastaa ilmoitettua, heitetään tulokseksi null.
         if (apu != riviLkm) {
            return null;
         }
         
         // Suljetaan tiedoston lukija.
         tiedostonLukija.close();

         // Palautetaan viite tulostaulukkoon.
         return kuvake;
      }
      
      // Nappaa tilanteet: tiedosto on tyhjä.
      catch (Exception e) {
         // Suljetaan tiedoston lukija tarvittaessa ja palautetaan tieto virheestä.
         if (tiedostonLukija != null) {
            tiedostonLukija.close();
         }
         return null;
      }
   }
             
   /*
   * Metodi tutkii annetusta tiedostosta rivien ja sarakkeiden määrän sekä tausta että edustamerkit
   * tallentaen ne olioon.
   * tiedostonLukija = Skanneri, joka lukee annettua tiedostoa (tiedosto kiinni lukijassa).
   * perusTiedot = Neljän alkion tyhjä taulukko, joka täytetään.
   */
   public static boolean tietoKaivaja(Scanner tiedostonLukija, String[] perusTiedot) {
      
      // Tarkistetaan syötteet, palautetaan tyhjillä arvoilla false.
      if (tiedostonLukija == null || perusTiedot == null) {
         return false;
      }
      
      // Luetaan perusTiedot-tiedoston ensimmäinen ja toinen rivi, jotta saadaan tietoon
      // kuvan koko. Rivien määrä n ja sarakkeiden määrä m.
      // Muutetaan String-muotoiset muuttujat int-muotoon.
      String rivi = tiedostonLukija.nextLine();
      int n = Integer.parseInt(rivi);
      rivi = tiedostonLukija.nextLine();
      int m = Integer.parseInt(rivi);

      if ((n < 3) || (m < 3)) {
         return false;
      }
      
      // Luetaan tiedoston 3. ja 4. rivi, jotka sisältävät tausta- ja
      // edustamerkit, tallennetaan ne char-muotoisina merkkeinä.
      String tausta = tiedostonLukija.nextLine();
      String edusta = tiedostonLukija.nextLine();

      // Päivitetään main-ohjelmassa olemaa perusTiedot-taulukkoa.
      // Luodaan neljän alkion String[]-tiedosto, jonka ensimmäinen alkio tulee olemaan
      // rivien lkm, toinen sarakkeiden lkm, kolmas taustamerkki, neljäs edustamerkki.
      perusTiedot[0] = Integer.toString(n);
      perusTiedot[1] = Integer.toString(m);
      perusTiedot[2] = tausta;
      perusTiedot[3] = edusta;
 
      return true;
   }

   /*
   * Metodi kopioi annetun 2d-taulukon.
   * taulu = Käyttäjän antama taulukko.
   */
   public static char[][] kopioiKuva(char[][] taulu, String[] perusTiedot) {

      // Tarkistetaan syötteet, palautetaan tyhjillä arvoilla null.
      if (taulu == null || perusTiedot == null) {
         return null;
      }
      
      // Otetaan perusTiedot-taulusta rivien ja sarakkeiden määrä, 
      // luodaan näillä tiedoilla tyhjä kuvaKopio.
      int riviLkm = Integer.parseInt(perusTiedot[0]);
      int sarakeLkm = Integer.parseInt(perusTiedot[1]);

      char[][] kuvaKopio = new char[riviLkm][sarakeLkm];

      // Käydään alkuperäinen taulu läpi ja kopioidaan alkiot kopioTauluun.
      for (int i = 0; i < riviLkm; i++) {
         for (int ind = 0; ind < taulu[i].length; ind++) {
            kuvaKopio[i][ind] = taulu[i][ind];
         }
      }  
      
      // Palautetaan viite kopioituun taulukkoon.
      return kuvaKopio;
   }
   
      /*
   * Metodi kopioi perusTiedot-taulukon.
   * perusTiedot = Tiedoston sisältämät neljä ensimmäistä riviä taulukossa.
   */
   public static String[] kopioiPerusTiedot(String[] perusTiedot) {

      // Tarkistetaan syötteet, palautetaan tyhjillä arvoilla null.
      if (perusTiedot == null) {
         return null;
      }

      String[] perusTiedotAlku = new String[4];

      // Käydään alkuperäinen taulu läpi ja kopioidaan alkiot perusTiedotAlkuun.
      for (int i = 0; i < 4; i++) {
         perusTiedotAlku[i] = perusTiedot[i];
      }  
      
      // Palautetaan viite kopioituun taulukkoon.
      return perusTiedotAlku;
   }
   
    
   /*
   * Metodi tarkistaa parametrina saadun taulukon arvojen oikeellisuuden ja
   * palauttaa viitteen taulukkoon, parametrit String[]-taulukossa tai virheilmoitus.
   * tarkistaTama = Käyttäjän antama käsky mahdollisine lisäparametreineen.
   */
   public static String[] tarkistaKomento(String tarkistaTama, String[] perusTiedot) {

      // Tarkistetaan syötteet, palautetaan tyhjillä arvoilla null.
      if (tarkistaTama == null || perusTiedot == null) {
         return null;
      }
      
      // Otetaan perusTiedot-taulusta rivien ja sarakkeiden määrä, 
      int riviLkm = Integer.parseInt(perusTiedot[0]);
      int sarakeLkm = Integer.parseInt(perusTiedot[1]);

      if (tarkistaTama != null) {
      
         // Pilkotaan syöte, jotta voidaan tarkistella komentoa.
         String[] tutkittava = tarkistaTama.split("[ ]");

         // Tarkistetaan ensin, onko komento-osa oikein. Palautetaan null, jos ei.
         if (!tutkittava[0].equals(TULOSTA) && !tutkittava[0].equals(VARIT) 
            && !tutkittava[0].equals(TIEDOT) && !tutkittava[0].equals(LATAA) 
            && !tutkittava[0].equals(KASVATA) && !tutkittava[0].equals(KUTISTA)
            && !tutkittava[0].equals(LOPETA)) {
            return null;
         }

         // Tarkistetaan, että muille kun kutista- ja kasvata-käskyille ei ole annettu käskyn
         // lisäksi parametreja.
         else if (!tutkittava[0].equals(KASVATA) && !tutkittava[0].equals(KUTISTA) && 
            tutkittava.length == 1) {
            return tutkittava;
         }
         
         // Kasvata/kutista -käskyillä parametreja pitää olla yksi parametri, joka ei saa olla
         // pienempi kuin kolme, parillinen tai suurempi kuin rivien tai sarakkeiden määrä.
         else if ((tutkittava[0].equals(KASVATA) || tutkittava[0].equals(KUTISTA)) 
            && tutkittava.length == 2) {
               
            int sivu = Integer.parseInt(tutkittava[1]);
               
            if ((sivu > 2) && (sivu % 2 != 0) && (sivu < riviLkm) && (sivu < sarakeLkm)) {
                  return tutkittava;  
            }
               
            else {
               return null;
            }
         }
      }
           
      return null;
   }

   /*
    * Metodi tulostaa käyttäjän 2d-taulukon alkiot.
    * taulu = Käyttäjän täyttämä taulukko.
    */
    
   public static void tulostaKuva(char[][] taulu) {

      // Tarkistetaan syötteet, suoritetaan sisältö jos parametri ei ole null.
      if (taulu != null) {
      
         // Tutkitaan ensin taulun rivien määrä.
         int vikaRivi = taulu.length;

         for (int i = 0; i < vikaRivi; i++) {
            // Tulostetaan jokaisen rivin luvut for-silmukalla. Rivin koko selvitetään
            // taulukon length-attribuutin avulla.
            for (int ind = 0; ind < taulu[i].length; ind++) {
               // Ei vaihdeta riviä, koska tulostetaan kaikki samalle riville.
               System.out.print(taulu[i][ind]);
            }
            // Jokainen kuvan rivi kuitenkin tulostetaan omalle rivilleen.
            System.out.println();
         }
      }
   } 
   
      /*
    * Metodi tarkistaa kaksiulotteisesta taulukosta, montako määritettyä merkkiä
    * se sisältää. Metodi tulostaa halutun infon ruudulle. 
    * tutkittava = Kuva, jonka eri alkioiden määrä halutaan tietää. 
    * perusTiedot = Taulukko, josta löytyy kuvan koko ja sen sisältämät merkit.
   */
   public static void haeTiedot(char[][] tutkittava, String[] perusTiedot) {

      // Tarkistetaan syötteet, suoritetaan sisältö jos parametri ei ole null.
      if (tutkittava != null && perusTiedot != null) {
      
         // Luodaan kaksi apumuuttujaa, yksi molemmille kuvan merkkien määrälle.
         int ekaLkm = 0;
         int tokaLkm = 0;
         
         // Lasketaan kuvasta molempien merkkien määrät.
         for (int i = 0; i < tutkittava.length; i++) {
               for (int ind = 0; ind < tutkittava[i].length; ind++) {
                  if (tutkittava[i][ind] == perusTiedot[2].charAt(0)) {
                     ekaLkm++;
                  }
                  else {
                     tokaLkm++;
                  }
               }               
         }    
                  
         System.out.println(perusTiedot[0] + " x " + perusTiedot[1]);
         System.out.println(perusTiedot[2] + " " + ekaLkm);
         System.out.println(perusTiedot[3] + " " + tokaLkm);
      }
   }   
   
   /*
   * Metodi vaihtaa taulukossa määrätyt merkit päikseen.
   * taulu = Annettu taulu.
   * korvaaTama = Ensimmäinen vaihtuva merkki.
   * korvaaTalla = Toinen vaihtuva merkki.
   */
   public static void vaihdaMerkit(char[][] taulu, String[] perusTiedot) {

      // Tarkistetaan syötteet, suoritetaan sisältö jos parametri ei ole null.
      if (taulu != null && perusTiedot != null) {
      
         char ekaVaihto = perusTiedot[2].charAt(0);
         char tokaVaihto = perusTiedot[3].charAt(0);

         // Käydään merkit läpi yksi kerrallaan. Korvataan jos on korvattavaa.
         for (int ind = 0; ind < taulu.length; ind++) {
            for (int i = 0; i < taulu[ind].length; i++) {
               if (taulu[ind][i] == ekaVaihto) {
                  taulu[ind][i] = tokaVaihto;
               }
               else if (taulu[ind][i] == tokaVaihto) {
                  taulu[ind][i] = ekaVaihto;
               }
            }
         }
      
         // Vaihdetaan perusTiedot-taulusta kahden viimeisen alkion paikkaa.
         String muuta = perusTiedot[2];
         perusTiedot[2] = perusTiedot[3];
         perusTiedot[3] = muuta;
      }
   }
   
   /* Metodi tekee neliökehyksen, jonka sivu on käyttäjän antama pariton parametri. Jos neliön keskipisteessä
   *  oleva merkki on taustamerkki ja kehyksen sisällä löytyy edustamerkki, keskusmerkki muuttuu. Kehys käy
   *  kuvan läpi kokonaisuudessaan.
   *  
   *  komento = String[]-taulukko, jonka alkioina komento ja kehyksen sivun koko.
   *  oikeaKuva = Alkuperäinen kuva, jonka perusteella muutos tehdään.
   *  perusTiedot = taulukko, jossa alkioina rivien ja sarakkeiden lkm, tausta- ja edustamerkit.
   */
  
   public static char[][] kasvataKuva(String[] komento, char[][] oikeaKuva, String[] perusTiedot) {
 
      // Tarkistetaan syötteet, palautetaan tyhjillä arvoilla null.
      if (komento == null || oikeaKuva == null || perusTiedot == null) {
         return null;
      }
           
      // Tallennetaan kehyksen sivun mitta muuttujaan, samoin puoliväli.
      int sivuMitta = Integer.parseInt(komento[1]);
      int keskiLuku = (1 + ((sivuMitta - 1 ) / 2));
      
      // Tehdään uusi, muutettava kuvakopio alkuperäisestä kuvasta. Ja merkit muuttujiin.
      char[][] muutettuKuva = kopioiKuva(oikeaKuva, perusTiedot);
      char tausta = perusTiedot[2].charAt(0);
      char edusta = perusTiedot[3].charAt(0);
      
      // Viimeinen tarkistettava alkio sijaitsee paikassa.
      int reuna = (sivuMitta - keskiLuku);
      int riviLkm = Integer.parseInt(perusTiedot[0]);
      int sarakeLkm = Integer.parseInt(perusTiedot[1]);
      int vikaRivi = (riviLkm - sivuMitta);
      int vikaSarake = (sarakeLkm - sivuMitta);
            
      for (int i = 0; i <= vikaRivi; i++) {
         for (int j = 0; j <= vikaSarake; j++) {
            if (oikeaKuva[keskiLuku - 1 + i][keskiLuku - 1 + j] == tausta) {
               for (int r = i; r < i + sivuMitta; r++) {
                  for (int s = j; s < j + sivuMitta; s++) {
                     if (oikeaKuva[r][s] == edusta) {
                        muutettuKuva[keskiLuku - 1 + i][keskiLuku -1 + j] = edusta;
                     }
                  }
               }
            }
         }
      }
      
      return muutettuKuva;
   
   }
  
   public static void main(String[] args) {

      // Tulostetaan otsikko.
      System.out.println("-----------------------");
      System.out.println("| Binary image editor |");
      System.out.println("-----------------------");
       
      // Asetetaan boolean-tyyppinen lopetuslippumuuttuja seis ja kaiku-muuttuja.
      boolean seis = false;
 
      // Luodaan neljän alkion String[]-tiedosto, jonka ensimmäinen alkio tulee olemaan
      // rivien lkm, toinen sarakkeiden lkm, kolmas etumerkki, neljäs taustamerkki.
      String[] perusTiedot = new String[4];
 
      // Tarkistetaan komentoriviparametrien oikeellisuus.
      boolean tulos = tutkiKomento(args);
      
      // Tarkistetaan kuvan oikeellisuus, jos komentorivi oli oikein.
      if (tulos == true) {
         // Tarkistetaan kuvan oikeellisuus ja ladataan se muuttujaan, jos 
         // kuva oikein.
         char[][] oikeaKuva = lataaKuva(args[0], perusTiedot);
            // Jos kuvassa virheitä, lopetetaan ohjelma.
            if (oikeaKuva == null) {
               System.out.println("Invalid image file!");
               seis = true;
            }
      
         if (seis == false) {
            // Kopioidaan kuva ohjelman käyttöä varten. Kopioidaan myös alkuperäiset perusTiedot ohjelman 
            // käyttöä varten.
            char[][] kuvanKopio = kopioiKuva(oikeaKuva, perusTiedot);
            String[] perusTiedotAlku = kopioiPerusTiedot(perusTiedot);
               if (kuvanKopio == null) {
                  seis = true;
               }
                  
            while (seis == false) {
               System.out.println("print/info/invert/dilate/erode/load/quit?");
               String toteuta = LUKIJA.nextLine();
               
               // Jos komentoriviparametriksi on annettu "echo", kaiutetaan käsky.
               if (args.length == 2) {
                  System.out.println(toteuta);
               }
               
               
               // Tarkistetaan komennon oikeellisuus.
               String[] komento = tarkistaKomento(toteuta, perusTiedot);
               
               // Jos komennossa häikkää, tulostetaan virheviesti.
               if (komento == null) {
                  System.out.println("Invalid command!");
               }
            
               // Toteutetaan haluttu komento.
               else {
                  if (komento[0].equals(TULOSTA)) {
                     tulostaKuva(kuvanKopio);
                  }
                  
                  else if (komento[0].equals(TIEDOT)) {
                     haeTiedot(kuvanKopio, perusTiedot);
                  }
                  
                  else if (komento[0].equals(VARIT)) {
                     vaihdaMerkit(kuvanKopio, perusTiedot);
                  }
                  
                  else if (komento[0].equals(KASVATA)) {
                     kuvanKopio = kasvataKuva(komento, kuvanKopio, perusTiedot);
                  }
                  
                  else if (komento[0].equals(KUTISTA)) {
                     // Luodaan valeTiedot, jossa merkit ovat vaihtaneet paikkaa.
                     String[] valeTiedot = new String[4];
                     valeTiedot[0] = perusTiedot[0];
                     valeTiedot[1] = perusTiedot[1];
                     valeTiedot[2] = perusTiedot[3];
                     valeTiedot[3] = perusTiedot[2];
                     
                     kuvanKopio = kasvataKuva(komento, kuvanKopio, valeTiedot);
                  }
                  
                  // Ladataan alkuperäinen tiedostosta luettu kuva sekä alkuperäiset
                  // perusTiedot-merkit.
                  else if (komento[0].equals(LATAA)) {
                     kuvanKopio = kopioiKuva(oikeaKuva, perusTiedot);
                     perusTiedot = kopioiPerusTiedot(perusTiedotAlku);
                  }           
                              
                  else if (komento[0].equals(LOPETA)) {
                     seis = true;
                  }
               }
            }
         }
      }
      
      System.out.println("Bye, see you soon.");
     
   }
}