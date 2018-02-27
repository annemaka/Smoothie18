
package tikape.runko.domain;


public class Ainesohje {
    
    private Integer jarjestys;
    private String maara;
    private String ohje;
    private Aines aines;

    public Ainesohje(Integer jarjestys, Aines aines, String maara, String ohje) {
        this.jarjestys = jarjestys;
        this.maara = maara;
        this.ohje = ohje;
        this.aines = aines;
    }

    public Aines getAines() {
        return aines;
    }

    public String getMaara() {
        return maara;
    }

    public String getOhje() {
        return ohje;
    }

    public Integer getJarjestys() {
        return jarjestys;
    }
    
}
