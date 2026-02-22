package org.myoggradio.stb;

import java.util.ArrayList;

public interface RundenSortierer 
{
	public void setGruppe(ArrayList<Auswertung> gruppe);
	public void sortierePartien(Runde runde);
}
