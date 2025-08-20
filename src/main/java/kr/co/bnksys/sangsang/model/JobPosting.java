package kr.co.bnksys.sangsang.model;

public class JobPosting {
    
    private Long id;
    
    private String 기관명;
    
    private String 공고명;
    
    private String 공고시작일;
    
    private String 공고마감일;
    
    private String 접수시작일;
    
    private String 접수마감일;
    
    private String 접수방법;
    
    private String 접수대행;
    
    private String 일반전형;
    
    private String 채용인원;
    
    private String 채용방법;
    
    private String 전형방법;
    
    private String 임용시기;
    
    private String 임용조건;
    
    private String 담당부서;
    
    private String 연락처;

    // 기본 생성자
    public JobPosting() {}

    // 전체 필드 생성자
    public JobPosting(String 기관명, String 공고명, String 공고시작일, String 공고마감일, 
                     String 접수시작일, String 접수마감일, String 접수방법, String 접수대행, 
                     String 일반전형, String 채용인원, String 채용방법, String 전형방법,
                     String 임용시기, String 임용조건, String 담당부서, String 연락처) {
        this.기관명 = 기관명;
        this.공고명 = 공고명;
        this.공고시작일 = 공고시작일;
        this.공고마감일 = 공고마감일;
        this.접수시작일 = 접수시작일;
        this.접수마감일 = 접수마감일;
        this.접수방법 = 접수방법;
        this.접수대행 = 접수대행;
        this.일반전형 = 일반전형;
        this.채용인원 = 채용인원;
        this.채용방법 = 채용방법;
        this.전형방법 = 전형방법;
        this.임용시기 = 임용시기;
        this.임용조건 = 임용조건;
        this.담당부서 = 담당부서;
        this.연락처 = 연락처;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String get기관명() {
        return 기관명;
    }

    public void set기관명(String 기관명) {
        this.기관명 = 기관명;
    }

    public String get공고명() {
        return 공고명;
    }

    public void set공고명(String 공고명) {
        this.공고명 = 공고명;
    }

    public String get공고시작일() {
        return 공고시작일;
    }

    public void set공고시작일(String 공고시작일) {
        this.공고시작일 = 공고시작일;
    }

    public String get공고마감일() {
        return 공고마감일;
    }

    public void set공고마감일(String 공고마감일) {
        this.공고마감일 = 공고마감일;
    }

    public String get접수시작일() {
        return 접수시작일;
    }

    public void set접수시작일(String 접수시작일) {
        this.접수시작일 = 접수시작일;
    }

    public String get접수마감일() {
        return 접수마감일;
    }

    public void set접수마감일(String 접수마감일) {
        this.접수마감일 = 접수마감일;
    }

    public String get접수방법() {
        return 접수방법;
    }

    public void set접수방법(String 접수방법) {
        this.접수방법 = 접수방법;
    }

    public String get접수대행() {
        return 접수대행;
    }

    public void set접수대행(String 접수대행) {
        this.접수대행 = 접수대행;
    }

    public String get일반전형() {
        return 일반전형;
    }

    public void set일반전형(String 일반전형) {
        this.일반전형 = 일반전형;
    }

    public String get채용인원() {
        return 채용인원;
    }

    public void set채용인원(String 채용인원) {
        this.채용인원 = 채용인원;
    }

    public String get채용방법() {
        return 채용방법;
    }

    public void set채용방법(String 채용방법) {
        this.채용방법 = 채용방법;
    }

    public String get전형방법() {
        return 전형방법;
    }

    public void set전형방법(String 전형방법) {
        this.전형방법 = 전형방법;
    }

    public String get임용시기() {
        return 임용시기;
    }

    public void set임용시기(String 임용시기) {
        this.임용시기 = 임용시기;
    }

    public String get임용조건() {
        return 임용조건;
    }

    public void set임용조건(String 임용조건) {
        this.임용조건 = 임용조건;
    }

    public String get담당부서() {
        return 담당부서;
    }

    public void set담당부서(String 담당부서) {
        this.담당부서 = 담당부서;
    }

    public String get연락처() {
        return 연락처;
    }

    public void set연락처(String 연락처) {
        this.연락처 = 연락처;
    }

    @Override
    public String toString() {
        return "JobPosting{" +
                "id=" + id +
                ", 기관명='" + 기관명 + '\'' +
                ", 공고명='" + 공고명 + '\'' +
                ", 공고시작일='" + 공고시작일 + '\'' +
                ", 공고마감일='" + 공고마감일 + '\'' +
                ", 접수시작일='" + 접수시작일 + '\'' +
                ", 접수마감일='" + 접수마감일 + '\'' +
                ", 접수방법='" + 접수방법 + '\'' +
                ", 접수대행='" + 접수대행 + '\'' +
                ", 일반전형='" + 일반전형 + '\'' +
                ", 채용인원='" + 채용인원 + '\'' +
                ", 채용방법='" + 채용방법 + '\'' +
                ", 전형방법='" + 전형방법 + '\'' +
                ", 임용시기='" + 임용시기 + '\'' +
                ", 임용조건='" + 임용조건 + '\'' +
                ", 담당부서='" + 담당부서 + '\'' +
                ", 연락처='" + 연락처 + '\'' +
                '}';
    }
}
