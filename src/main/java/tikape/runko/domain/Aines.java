package tikape.runko.domain;

public class Aines {

    private Integer id;
    private String nimi;

    public Aines(Integer id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    public static class AinesTilasto {
        private String nimi;
        private Integer lukumaara;
        private double keskiarvo;
        private double kokonaismaara;

        public AinesTilasto(String nimi, Integer lukumaara, double keskiarvo, double kokonaismaara) {
            this.nimi = nimi;
            this.lukumaara = lukumaara;
            this.keskiarvo = keskiarvo;
            this.kokonaismaara = kokonaismaara;
        }

        public double getKeskiarvo() {
            return keskiarvo;
        }

        public double getKokonaismaara() {
            return kokonaismaara;
        }

        public Integer getLukumaara() {
            return lukumaara;
        }

        public String getNimi() {
            return nimi;
        }
    }

}
