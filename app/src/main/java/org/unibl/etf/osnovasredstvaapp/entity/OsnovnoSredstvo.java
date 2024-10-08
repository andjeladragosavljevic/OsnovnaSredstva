package org.unibl.etf.osnovasredstvaapp.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "osnovna_sredstva",
        foreignKeys = {
                @ForeignKey(entity = Zaposleni.class, parentColumns = "id", childColumns = "zaduzenaOsobaId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Lokacija.class, parentColumns = "id", childColumns = "zaduzenaLokacijaId", onDelete = ForeignKey.CASCADE)
        }
)

public class OsnovnoSredstvo implements Serializable {
   @PrimaryKey(autoGenerate = true)
    private int id;

   private String naziv;
   private String opis;
   private String barkod;
   private double cijena;
   private String datumKreiranja;
   private int zaduzenaOsobaId;
   private int zaduzenaLokacijaId;
    private String slikaPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getBarkod() {
        return barkod;
    }

    public void setBarkod(String barkod) {
        this.barkod = barkod;
    }

    public double getCijena() {
        return cijena;
    }

    public void setCijena(double cijena) {
        this.cijena = cijena;
    }

    public String getDatumKreiranja() {
        return datumKreiranja;
    }

    public void setDatumKreiranja(String datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }

    public int getZaduzenaOsobaId() {
        return zaduzenaOsobaId;
    }

    public void setZaduzenaOsobaId(int zaduzenaOsobaId) {
        this.zaduzenaOsobaId = zaduzenaOsobaId;
    }

    public int getZaduzenaLokacijaId() {
        return zaduzenaLokacijaId;
    }

    public void setZaduzenaLokacijaId(int zaduzenaLokacijaId) {
        this.zaduzenaLokacijaId = zaduzenaLokacijaId;
    }

    public String getSlikaPath() {
        return slikaPath;
    }

    public void setSlikaPath(String slikaPath) {
        this.slikaPath = slikaPath;
    }
}
