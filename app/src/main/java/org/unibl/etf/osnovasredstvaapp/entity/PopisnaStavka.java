package org.unibl.etf.osnovasredstvaapp.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serial;
import java.io.Serializable;

@Entity(tableName = "popisna_stavka",
        foreignKeys = {
        @ForeignKey(entity = OsnovnoSredstvo.class, parentColumns = "id", childColumns = "osnovnoSredstvoId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Zaposleni.class, parentColumns = "id", childColumns = "trenutnaOsobaId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Zaposleni.class, parentColumns = "id", childColumns = "novaOsobaId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Lokacija.class, parentColumns = "id", childColumns = "trenutnaLokacijaId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Lokacija.class, parentColumns = "id", childColumns = "novaLokacijaId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = PopisnaLista.class, parentColumns = "id", childColumns = "popisnaListaId",  onDelete = ForeignKey.CASCADE)
        }
)
public class PopisnaStavka implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int osnovnoSredstvoId;
    private int trenutnaOsobaId;
    private int novaOsobaId;
    private int trenutnaLokacijaId;
    private int novaLokacijaId;
    private int popisnaListaId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOsnovnoSredstvoId() {
        return osnovnoSredstvoId;
    }

    public void setOsnovnoSredstvoId(int osnovnoSredstvoId) {
        this.osnovnoSredstvoId = osnovnoSredstvoId;
    }

    public int getTrenutnaOsobaId() {
        return trenutnaOsobaId;
    }

    public void setTrenutnaOsobaId(int trenutnaOsobaId) {
        this.trenutnaOsobaId = trenutnaOsobaId;
    }

    public int getNovaOsobaId() {
        return novaOsobaId;
    }

    public void setNovaOsobaId(int novaOsobaId) {
        this.novaOsobaId = novaOsobaId;
    }

    public int getTrenutnaLokacijaId() {
        return trenutnaLokacijaId;
    }

    public void setTrenutnaLokacijaId(int trenutnaLokacijaId) {
        this.trenutnaLokacijaId = trenutnaLokacijaId;
    }

    public int getNovaLokacijaId() {
        return novaLokacijaId;
    }

    public void setNovaLokacijaId(int novaLokacijaId) {
        this.novaLokacijaId = novaLokacijaId;
    }

    public int getPopisnaListaId() {
        return popisnaListaId;
    }

    public void setPopisnaListaId(int popisnaListaId) {
        this.popisnaListaId = popisnaListaId;
    }
}
