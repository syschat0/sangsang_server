package kr.co.bnksys.sangsang.model;

public class RecommendResponse {
    private String id;
    private String 기관명;
    private String 일반전형;
    private Double 유사도;

    public RecommendResponse() {}

    public RecommendResponse(String id, String 기관명, String 일반전형, Double 유사도) {
        this.id = id;
        this.기관명 = 기관명;
        this.일반전형 = 일반전형;
        this.유사도 = 유사도;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get기관명() {
        return 기관명;
    }

    public void set기관명(String 기관명) {
        this.기관명 = 기관명;
    }

    public String get일반전형() {
        return 일반전형;
    }

    public void set일반전형(String 일반전형) {
        this.일반전형 = 일반전형;
    }

    public Double get유사도() {
        return 유사도;
    }

    public void set유사도(Double 유사도) {
        this.유사도 = 유사도;
    }

    @Override
    public String toString() {
        return "RecommendResponse{" +
                "id='" + id + '\'' +
                ", 기관명='" + 기관명 + '\'' +
                ", 일반전형='" + 일반전형 + '\'' +
                ", 유사도=" + 유사도 +
                '}';
    }
}
