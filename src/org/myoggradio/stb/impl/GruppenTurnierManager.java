package org.myoggradio.stb.impl;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JOptionPane;
import org.myoggradio.stb.*;
public class GruppenTurnierManager implements TurnierManager
{
	private Turnier turnier = null;
	private int bewerteGleicheFarbeHintereinander(Runde runde,ArrayList<Auswertung> gruppe)
	{
		int erg = 0;
		for (int i=0;i<gruppe.size();i++)
		{
			Auswertung auswertung = gruppe.get(i);
			Spieler spieler = auswertung.getSpieler();
			int anzahlWeissHintereinander = auswertung.getAnzahlWeissHintereinander();
			int anzahlSchwarzHintereinander = auswertung.getAnzahlSchwarzHintereinander();
			if (runde != null)
			{
				int anzahlWeiss = getAnzahlWeiss(spieler,runde);
				int anzahlSchwarz = getAnzahlSchwarz(spieler,runde);
				if (anzahlWeiss > 0)
				{
					anzahlWeissHintereinander++;
					anzahlSchwarzHintereinander = 0;
				}
				if (anzahlSchwarz > 0)
				{
					anzahlSchwarzHintereinander++;
					anzahlWeissHintereinander = 0;
				}
				if (anzahlWeissHintereinander >= 2) erg += 1;
				if (anzahlSchwarzHintereinander >= 2) erg += 1;
			}
		}
		return erg;
	}
	public double getAnzahlPunkte(Spieler spieler,ArrayList<Auswertung> gruppe)
	{
		double erg = 0.0;
		for (int i=0;i<gruppe.size();i++)
		{
			Auswertung auswertung = gruppe.get(i);
			Spieler test = auswertung.getSpieler();
			if (test.istGleich(spieler))
			{
				erg += auswertung.getPunkte();
				erg += auswertung.getBuchholz() / 10000.0;
				erg += auswertung.getSonneberger() / (10000.0 * 10000.0);
			}
		}
		return erg;
	}
	private int bewerteGleichGuteSpielerSolltenGegeneinaderSpielen(Runde runde,ArrayList<Auswertung> gruppe)
	{
		int erg = 0;
		for (int i=0;i<runde.getMaxPartien();i++)
		{
			Partie partie = runde.getPartie(i);
			Spieler spieler1 = partie.getWeiss();
			Spieler spieler2 = partie.getSchwarz();
			double punktew = getAnzahlPunkte(spieler1,gruppe);
			double punktes = getAnzahlPunkte(spieler2,gruppe);
			double delta = punktew - punktes;
			if (delta < 0) delta = punktes - punktew;
			erg += (delta * Parameter.malusGleichGut);
		}
		return erg;
	}
	private int bewerteDieFarbdifferenzEinesSpielersSollteKleiner2Sein(Runde runde,ArrayList<Auswertung> gruppe)
	{
		int erg = 0;
		for (int i=0;i<gruppe.size();i++)
		{
			Auswertung auswertung = gruppe.get(i);
			Spieler spieler = auswertung.getSpieler();
			int anzahlWeiss = auswertung.getAnzahlWeiss();
			int anzahlSchwarz = auswertung.getAnzahlSchwarz();
			if (runde != null)
			{
				anzahlWeiss+=getAnzahlWeiss(spieler,runde);
				anzahlSchwarz+=getAnzahlSchwarz(spieler,runde);
				int delta = anzahlWeiss-anzahlSchwarz;
				if (delta < 0) delta = anzahlSchwarz-anzahlWeiss;
				if (delta > 1) erg += Parameter.malusFarbdifferenz2;
			}
		}
		return erg;
	}
	private int bewerteKeinSpielerDarfDreimalHintereinanderDieGleicheFarbeHaben(Runde runde,ArrayList<Auswertung> gruppe)
	{
		int erg = 0;
		for (int i=0;i<gruppe.size();i++)
		{
			Auswertung auswertung = gruppe.get(i);
			Spieler spieler = auswertung.getSpieler();
			int anzahlWeissHintereinander = auswertung.getAnzahlWeissHintereinander();
			int anzahlSchwarzHintereinander = auswertung.getAnzahlSchwarzHintereinander();
			if (runde != null)
			{
				int anzahlWeiss = getAnzahlWeiss(spieler,runde);
				int anzahlSchwarz = getAnzahlSchwarz(spieler,runde);
				if (anzahlWeiss > 0)
				{
					anzahlWeissHintereinander++;
					anzahlSchwarzHintereinander = 0;
				}
				if (anzahlSchwarz > 0)
				{
					anzahlSchwarzHintereinander++;
					anzahlWeissHintereinander = 0;
				}
				if (anzahlWeissHintereinander == 3) erg += Parameter.malus3malGleicheFarbe;
				if (anzahlSchwarzHintereinander == 3) erg += Parameter.malus3malGleicheFarbe;
			}
			
		}
		return erg;
	}
	private int bewerteDieFarbdifferenzEinesSpielersMussKleiner3Sein(Runde runde,ArrayList<Auswertung> gruppe)
	{
		int erg = 0;
		for (int i=0;i<gruppe.size();i++)
		{
			Auswertung auswertung = gruppe.get(i);
			Spieler spieler = auswertung.getSpieler();
			int anzahlWeiss = auswertung.getAnzahlWeiss();
			int anzahlSchwarz = auswertung.getAnzahlSchwarz();
			if (runde != null)
			{
				anzahlWeiss+=getAnzahlWeiss(spieler,runde);
				anzahlSchwarz+=getAnzahlSchwarz(spieler,runde);
				int delta = anzahlWeiss-anzahlSchwarz;
				if (delta < 0) delta = anzahlSchwarz-anzahlWeiss;
				if (delta > 2)
				{
					erg += Parameter.malusFarbdifferenz3;
				}
			}
		}
		return erg;
	}
	private int bewerteRunde(Runde runde,ArrayList<Auswertung> gruppe,Turnier turnier)
	{
		int erg = 0;
		erg += bewerteDieFarbdifferenzEinesSpielersMussKleiner3Sein(runde,gruppe);
		erg += bewerteKeinSpielerDarfDreimalHintereinanderDieGleicheFarbeHaben(runde,gruppe);
		erg += bewerteDieFarbdifferenzEinesSpielersSollteKleiner2Sein(runde,gruppe);
		erg += bewerteGleichGuteSpielerSolltenGegeneinaderSpielen(runde,gruppe);
		erg += bewerteGleicheFarbeHintereinander(runde,gruppe);
		return erg;
	}
	private boolean istPartieSchonVorgekommen(Partie partie,Turnier turnier)
	{
		boolean erg = false;
		for (int i=0;i<turnier.getMaxrunden();i++)
		{
			Runde runde = turnier.getRunde(i);
			if (runde != null)
			{
				int max = runde.getMaxPartien();
				for (int j=0;j<max;j++)
				{
					Partie test = runde.getPartie(j);
					if (partieIstGleich(test,partie))
					{
						erg = true;
						break;
					}
				}
			}
		}
		return erg;
	}
	private Runde getZufaelligeRunde(ArrayList<Auswertung> gruppe,Turnier turnier)
	{
		ArrayList<Integer> nummern = new ArrayList<Integer>();
		for (int i=0;i<gruppe.size();i++)
		{
			nummern.add(i);
		}
		Runde erg = Factory.getRunde();
		int nhalbe = gruppe.size() / 2; // Beachte gruppe.size() ist gerade
		erg.setMaxPartien(nhalbe);
		for (int i=0;i<nhalbe;i++)
		{
			Partie partie = Factory.getPartie();
			int derzeitigeGruppenGroesse = nummern.size();
			double d = Math.random();
			double dauswahl = d * ((double) derzeitigeGruppenGroesse);
			int auswahl = (int) dauswahl;
			partie.setWeiss(gruppe.get(nummern.get(auswahl)).getSpieler());
			nummern.remove(auswahl);
			derzeitigeGruppenGroesse--;
			d = Math.random();
			dauswahl = d * ((double) derzeitigeGruppenGroesse);
			auswahl = (int) dauswahl;
			partie.setSchwarz(gruppe.get(nummern.get(auswahl)).getSpieler());
			if (istPartieSchonVorgekommen(partie,turnier))
			{
				erg = null;
				return erg;
			}
			nummern.remove(auswahl);
			erg.setPartie(partie,i);
		}
		return erg;
	}
	private Runde getBesteRunde(ArrayList<Auswertung> gruppe,Turnier turnier)
	{
		Runde erg = Factory.getRunde();
		int maxiter = Parameter.maxiter;
		if (gruppe.size() < 9) maxiter = maxiter / 10;
		else if (gruppe.size() < 13) maxiter = maxiter / 5; // Kleine Gruppen brauchen weniger Iterationen
		else if (gruppe.size() < 17) maxiter = maxiter / 2;
		Protokol.write("GruppenTurnierManager:getBesteRunde:Maxiter: " + maxiter);
		int nhalbe = gruppe.size() / 2; // Beachte gruppe.size() ist gerade
		erg.setMaxPartien(nhalbe);
		//
		int besteBewertung = Integer.MAX_VALUE;
		int anzahlNull = 0;
		int anzahlOK = 0;
		int iter = 0;
		int tempiter = 0;
		for (int i=0;i<maxiter;i++)
		{
			iter++;
			tempiter++;
			if (tempiter >= Parameter.itermsg)
			{
				tempiter = 0;
				Protokol.write("GruppenTurnierManager:getBesteRunde:Anzahl Iterationen: " + iter);
			}
			Runde zufaelligeRunde = getZufaelligeRunde(gruppe,turnier);
			if (zufaelligeRunde != null)
			{
				anzahlOK++;
				int bewertung = bewerteRunde(zufaelligeRunde,gruppe,turnier);
				if (bewertung < besteBewertung)
				{
					besteBewertung = bewertung;
					erg = zufaelligeRunde;
				}
			}
			else
			{
				anzahlNull++;
			}
		}
		Protokol.write("GruppenTurnierManager:getBesteRunde:Anzahl Null:" + anzahlNull);
		Protokol.write("GruppenTurnierManager:getBesteRunde:Anzahl OK  :" + anzahlOK);
		// Tausche jeweils Weiss und Schwarz und prüfe ob besser
		for (int i=0;i<erg.getMaxPartien();i++)
		{
			Partie partie = erg.getPartie(i);
			Spieler weiss = partie.getWeiss();
			Spieler schwarz = partie.getSchwarz();
			Partie testPartie = Factory.getPartie();
			testPartie.setWeiss(schwarz);
			testPartie.setSchwarz(weiss);
			erg.setPartie(testPartie,i);
			int bewertung = bewerteRunde(erg, gruppe, turnier);
			if (bewertung < besteBewertung)
			{
				besteBewertung = bewertung;
			}
			else
			{
				erg.setPartie(partie,i);
			}
		}
		Protokol.write("GruppenTurnierManager:getBesteRunde:beste Bewertung: " + besteBewertung);
		RundenSortierer sortierer = Factory.getRundenSortierer();
		sortierer.setGruppe(gruppe); 
		sortierer.sortierePartien(erg);
		return erg;
	}
	private boolean hatteSchonFreilos(Spieler spieler,Turnier turnier)
	{
		boolean erg = false;
		int maxrunden = turnier.getMaxrunden();
		//int nummerAktiveRunde = turnier.getNummerAktiveRunde();
		for (int i=0;i<maxrunden;i++)
		{
			Runde runde = turnier.getRunde(i);
			if (runde != null)
			{
				ArrayList<Spieler> freilose = runde.getFreilos();
				for (int j=0;j<freilose.size();j++)
				{
					Spieler freilos = freilose.get(j);
					if (freilos.istGleich(spieler))
					{
						erg = true;
						break;
					}
				}
			}
		}
		return erg;
	}
	@Override
	public Runde starteNaechsteRunde(Turnier turnier)
	{
		Runde erg = Factory.getRunde();
		if (turnier.getMaxrunden()-1 > turnier.getNummerAktiveRunde())
		{
			Protokol.write("GruppenTurnierManager:starteNaechsteRunde: " + (turnier.getNummerAktiveRunde()+2));
			Parameter.auswertungen = getAuswertung(turnier.getNummerAktiveRunde());
			int n = Parameter.auswertungen.size();
			int nhalbe = n / 2;
			erg.setMaxPartien(nhalbe); 
			// Bei ungerader Anzahl Spieler muss ein Spieler Freilos bekommen
			if (n != 2 * nhalbe) // Anzahl Spieler ist ungerade. Einer muss Freilos haben
			{
				for (int i=0;i<n;i++) // Spieler in Reihenfolge vom schlechtesten bis zum Besten durchgehen
				{
					Spieler test = Parameter.auswertungen.get(n-i-1).getSpieler(); 
					if (!hatteSchonFreilos(test,turnier)) // Spieler hatte noch kein Freilos?
					{
						erg.addFreilos(test); // Spieler bekommt nun Freilos
						Parameter.auswertungen.remove(n-i-1); // Freilos Spieler wird aus den Auswertungen entfernt
						break; // fertig
					}
				}
			}
			// Jetzt ist die Anzahl Auswertungen und damit Spieler gerade
			// Teile nun die Auswertungen in Gruppen auf
			int minimaleGruppenGroesse = (turnier.getNummerAktiveRunde()+2) + Parameter.gruppenGroessenInkrement;
			minimaleGruppenGroesse = 2 * (minimaleGruppenGroesse / 2); // gruppenGroesse muss gerage sein
			Protokol.write("GruppenTurnierManager:starteNaechsteRunde:Minimale Gruppen Groesse: " + minimaleGruppenGroesse);
			int anzahlGruppen = n / minimaleGruppenGroesse;
			if (anzahlGruppen == 0) anzahlGruppen = 1; // es muss mindestens eine Gruppe geben
			int[] gruppenGroessen = new int[anzahlGruppen];
			for (int i=0;i<gruppenGroessen.length;i++) // Gruppen Größen initialisieren
			{
				gruppenGroessen[i] = 0;
			}
			// Gruppen Größen ermitteln
			int j = 0;
			for (int i=0;i<nhalbe;i++)
			{
				gruppenGroessen[j] = gruppenGroessen[j] + 2;
				j++;
				if (j == anzahlGruppen) j = 0;
			}
			// Gruppen ermitteln
			ArrayList<ArrayList<Auswertung>> gruppen = new ArrayList<ArrayList<Auswertung>>();
			int pos = 0;
			for (int i=0;i<anzahlGruppen;i++)
			{
				ArrayList<Auswertung> gruppe = new ArrayList<Auswertung>();
				int gruppenGroesse = gruppenGroessen[i];
				for (j=0;j<gruppenGroesse;j++)
				{
					Auswertung auswertung = Parameter.auswertungen.get(pos);
					pos++;
					gruppe.add(auswertung);
				}
				gruppen.add(gruppe);
			}
			for (int i=0;i<anzahlGruppen;i++)
			{
				int gruppenGroesse = gruppen.get(i).size();
				Protokol.write("GruppenTurnierManager:starteNaechsteRunde:Gruppe " + (i+1) + " Groesse " + gruppenGroesse);
			}
			// Führe die Runden aller Gruppen zu einer gesamt Runde zusammen 
			int partieNummer = 0;
			for (int i=0;i<gruppen.size();i++)
			{
				ArrayList<Auswertung> aktuelleGruppe = gruppen.get(i);
				Protokol.write("GruppenTurnierManager:starteNaechsteRunde:Begin Gruppe " + (i+1) + " von " + gruppen.size() +  " Gruppengroesse: " + aktuelleGruppe.size());
				Runde runde = getBesteRunde(aktuelleGruppe,turnier);
				for (j=0;j<runde.getMaxPartien();j++)
				{
					Partie partie = runde.getPartie(j);
					erg.setPartie(partie,partieNummer);
					partieNummer++;
				}
				Protokol.write("GruppenTurnierManager:starteNaechsteRunde:Ende  Gruppe " + (i+1) + " von " + gruppen.size());
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null,"Letzte Runde bereits erzeugt","Fehler",JOptionPane.INFORMATION_MESSAGE);
		}
		return erg;
	}
	private boolean partieIstGleich(Partie p1,Partie p2)
	{
		boolean erg = false;
		Spieler w1 = p1.getWeiss();
		Spieler s1 = p1.getSchwarz();
		Spieler w2 = p2.getWeiss();
		Spieler s2 = p2.getSchwarz();
		if (s1.istGleich(s2) && w1.istGleich(w2)) erg = true;
		if (s1.istGleich(w2) && w1.istGleich(s2)) erg = true;
		return erg;
	}
	private int getAnzahlWeiss(Spieler spieler,Runde runde)
	{
		int erg = 0;
		if (runde != null)
		{
			for (int i=0;i<runde.getMaxPartien();i++)
			{
				Partie partie = runde.getPartie(i);
				Spieler weiss = partie.getWeiss();
				if (weiss.istGleich(spieler)) erg++;
			}
		}
		return erg;
	}
	private int getAnzahlSchwarz(Spieler spieler,Runde runde)
	{
		int erg = 0;
		if (runde != null)
		{
			for (int i=0;i<runde.getMaxPartien();i++)
			{
				Partie partie = runde.getPartie(i);
				Spieler schwarz = partie.getSchwarz();
				if (schwarz.istGleich(spieler)) erg++;
			}
		}
		return erg;
	}
	@Override
	public ArrayList<Auswertung> getAuswertung(int rundenNummer) 
	{
		ArrayList<Auswertung> erg = new ArrayList<Auswertung>();
		for (int i=0;i<Parameter.turnier.getSpieler().size();i++)
		{
			Spieler spieler = Parameter.turnier.getSpieler().get(i);
			double punkte = 0.0;
			int anzahlWeiss = 0;
			int anzahlSchwarz = 0;
			int anzahlWeissHintereinander = 0;
			int anzahlSchwarzHintereinander = 0;
			for (int x=0;x<=rundenNummer;x++)
			{
				Runde runde = Parameter.turnier.getRunde(x);
				ArrayList<Spieler> freilos = runde.getFreilos();
				if (freilos != null)
				{
					for (int c=0;c<freilos.size();c++)
					{
						if (freilos.get(c).istGleich(spieler)) punkte = punkte + Parameter.kampflos_schweizer;
					}
				}
				for (int a=0;a<runde.getMaxPartien();a++)
				{
					Partie partie = runde.getPartie(a);
					Spieler weiss = partie.getWeiss();
					Spieler schwarz = partie.getSchwarz();
					int ergebnis = partie.getErgebnisN();
					if (spieler.istGleich(weiss))
					{
						anzahlWeiss++;
						if (ergebnis == 1) punkte += 0.5;
						if (ergebnis == 2 | ergebnis == 4) punkte += 1.0;
					}
					if (spieler.istGleich(schwarz))
					{
						anzahlSchwarz++;
						if (ergebnis == 1) punkte += 0.5;
						if (ergebnis == 3 | ergebnis == 5) punkte += 1.0;
					}
				}
				for (int a=0;a<runde.getMaxPartien();a++)
				{
					Partie partie = runde.getPartie(a);
					Spieler weiss = partie.getWeiss();
					Spieler schwarz = partie.getSchwarz();
					if (spieler.istGleich(weiss))
					{
						anzahlWeissHintereinander++;
						anzahlSchwarzHintereinander = 0;
					}
					if (spieler.istGleich(schwarz))
					{
						anzahlSchwarzHintereinander++;
						anzahlWeissHintereinander = 0;
					}
				}
			}
			Auswertung auswertung = new Auswertung();
			auswertung.setSpieler(spieler);
			auswertung.setPunkte(punkte);
			auswertung.setAnzahlWeiss(anzahlWeiss);
			auswertung.setAnzahlSchwarz(anzahlSchwarz);
			auswertung.setAnzahlWeissHintereinander(anzahlWeissHintereinander);
			auswertung.setAnzahlSchwarzHintereinander(anzahlSchwarzHintereinander);
			erg.add(auswertung);
		}
		for (int i=0;i<Parameter.turnier.getSpieler().size();i++)
		{
			Auswertung auswertung = erg.get(i);
			double buchholz = 0.0;
			Spieler spieler = auswertung.getSpieler();
			for (int x=0;x<=rundenNummer;x++)
			{
				Runde runde = Parameter.turnier.getRunde(x);
				for (int a=0;a<runde.getMaxPartien();a++)
				{
					Partie partie = runde.getPartie(a);
					Spieler weiss = partie.getWeiss();
					Spieler schwarz = partie.getSchwarz();
					if (spieler.istGleich(weiss))
					{
						for (int k=0;k<erg.size();k++)
						{
							Auswertung test = erg.get(k);
							Spieler testspieler = test.getSpieler();
							if (testspieler.istGleich(schwarz))
							{
								buchholz += test.getPunkte();
							}
						}
					}
					if (spieler.istGleich(schwarz))
					{
						for (int k=0;k<erg.size();k++)
						{
							Auswertung test = erg.get(k);
							Spieler testspieler = test.getSpieler();
							if (testspieler.istGleich(weiss))
							{
								buchholz += test.getPunkte();
							}
						}
					}
				}
			}
			auswertung.setBuchholz(buchholz);
		}
		for (int i=0;i<Parameter.turnier.getSpieler().size();i++)
		{
			Auswertung auswertung = erg.get(i);
			double sonneberger = 0.0;
			Spieler spieler = auswertung.getSpieler();
			for (int x=0;x<=rundenNummer;x++)
			{
				Runde runde = Parameter.turnier.getRunde(x);
				for (int a=0;a<runde.getMaxPartien();a++)
				{
					Partie partie = runde.getPartie(a);
					Spieler weiss = partie.getWeiss();
					Spieler schwarz = partie.getSchwarz();
					if (spieler.istGleich(weiss))
					{
						for (int k=0;k<erg.size();k++)
						{
							Auswertung test = erg.get(k);
							Spieler testspieler = test.getSpieler();
							if (testspieler.istGleich(schwarz))
							{
								int partieergebnis = partie.getErgebnisN();
								if (partieergebnis == 2 | partieergebnis == 4) //Weiss hat gewonnen
								{
									sonneberger += test.getPunkte();
								}
								else if (partieergebnis == 1) //Unentschieden
								{
									sonneberger += test.getPunkte() / 2.0;
								}
							}
						}
					}
					if (spieler.istGleich(schwarz))
					{
						for (int k=0;k<erg.size();k++)
						{
							Auswertung test = erg.get(k);
							Spieler testspieler = test.getSpieler();
							if (testspieler.istGleich(weiss))
							{
								int partieergebnis = partie.getErgebnisN();
								if (partieergebnis == 3 | partieergebnis == 5) //Schwarz hat gewonnen
								{
									sonneberger += test.getPunkte();
								}
								else if (partieergebnis == 1) //Unentschieden
								{
									sonneberger += test.getPunkte() / 2.0;
								}
							}
						}
					}

				}
			}
			auswertung.setSonneberger(sonneberger);
		}
		Collections.sort(erg,new AuswertungComparator());
		return erg;
	}
	@Override
	public void changeSpieler(Spieler alt, Spieler neu) 
	{
		for (int i=0;i<Parameter.turnier.getMaxrunden();i++)
		{
			Runde runde = Parameter.turnier.getRunde(i);
			if (runde != null)
			{
				runde.changeSpieler(alt,neu);
			}
		}
		ArrayList<Spieler> spieler = Parameter.turnier.getSpieler();
		if (spieler != null)
		{
			for (int i=0;i<spieler.size();i++)
			{
				Spieler test = spieler.get(i);
				if (test.istGleich(alt))
				{
					test.setId(neu.getId());
					test.setVorname(neu.getVorname());
					test.setName(neu.getName());
					test.setDWZ(neu.getDWZ());
				}
			}
		}
		if (turnier != null)
		{
			for (int i=0;i<turnier.getMaxrunden();i++)
			{
				Runde runde = turnier.getRunde(i);
				if (runde != null)
				{
					runde.changeSpieler(alt,neu);
				}
			}
			spieler = turnier.getSpieler();
			if (spieler != null)
			{
				for (int i=0;i<spieler.size();i++)
				{
					Spieler test = spieler.get(i);
					if (test.istGleich(alt))
					{
						test.setId(neu.getId());
						test.setVorname(neu.getVorname());
						test.setName(neu.getName());
						test.setDWZ(neu.getDWZ());
					}
				}
			}
		}
	}
}
