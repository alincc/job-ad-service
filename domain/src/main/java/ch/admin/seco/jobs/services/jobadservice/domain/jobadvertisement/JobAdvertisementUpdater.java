package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.admin.seco.jobs.services.jobadservice.core.domain.events.AuditUser;

public class JobAdvertisementUpdater {

    static final String SECTION_FINGERPRINT = "SECTION_FINGERPRINT";
    static final String SECTION_X28_OCCUPATION_CODES = "SECTION_X28_OCCUPATION_CODES";
    static final String SECTION_SOURCE_ENTRY_ID = "SECTION_SOURCE_ENTRY_ID";
    static final String SECTION_EXTERNAL_URL = "SECTION_EXTERNAL_URL";
    static final String SECTION_REPORTING_OBLIGATION = "SECTION_REPORTING_OBLIGATION";
    static final String SECTION_PUBLICATION = "SECTION_PUBLICATION";
    static final String SECTION_EMPLOYMENT = "SECTION_EMPLOYMENT";
    static final String SECTION_JOB_CENTER_CODE = "SECTION_JOB_CENTER_CODE";
    static final String SECTION_APPLY_CHANNEL = "SECTION_APPLY_CHANNEL";
    static final String SECTION_COMPANY = "SECTION_COMPANY";
    static final String SECTION_CONTACT = "SECTION_CONTACT";
    static final String SECTION_LOCATION = "SECTION_LOCATION";
    static final String SECTION_OCCUPATIONS = "SECTION_OCCUPATIONS";
    static final String SECTION_LANGUAGE_SKILLS = "SECTION_LANGUAGE_SKILLS";

    private Set<String> changedSections;

    private AuditUser auditUser;

    private String fingerprint;

    private String x28OccupationCodes;

    private String externalReference;

    private String externalUrl;

    private boolean reportingObligation;

    private LocalDate reportingObligationEndDate;

    private Employment employment;

    private String jobCenterCode;

    private ApplyChannel applyChannel;

    private Company company;

    private Contact contact;

    private Location location;

    private List<Occupation> occupations;

    private List<LanguageSkill> languageSkills;

    private Publication publication;

    public JobAdvertisementUpdater(Builder builder) {
        this.changedSections = builder.changedSections;
        this.auditUser = builder.auditUser;
        this.fingerprint = builder.fingerprint;
        this.x28OccupationCodes = builder.x28OccupationCodes;
        this.externalReference = builder.externalReference;
        this.externalUrl = builder.externalUrl;
        this.reportingObligation = builder.reportingObligation;
        this.reportingObligationEndDate = builder.reportingObligationEndDate;
        this.employment = builder.employment;
        this.jobCenterCode = builder.jobCenterCode;
        this.applyChannel = builder.applyChannel;
        this.company = builder.company;
        this.contact = builder.contact;
        this.location = builder.location;
        this.occupations = builder.occupations;
        this.languageSkills = builder.languageSkills;
        this.publication = builder.publication;
    }

    public boolean hasAnyChangesIn(String section) {
        return changedSections.contains(section);
    }

    public AuditUser getAuditUser() {
        return auditUser;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getX28OccupationCodes() {
        return x28OccupationCodes;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public boolean isReportingObligation() {
        return reportingObligation;
    }

    public LocalDate getReportingObligationEndDate() {
        return reportingObligationEndDate;
    }

    public Employment getEmployment() {
        return employment;
    }

    public String getJobCenterCode() {
        return jobCenterCode;
    }

    public ApplyChannel getApplyChannel() {
        return applyChannel;
    }

    public Company getCompany() {
        return company;
    }

    public Contact getContact() {
        return contact;
    }

    public Location getLocation() {
        return location;
    }

    public List<Occupation> getOccupations() {
        return occupations;
    }

    public List<LanguageSkill> getLanguageSkills() {
        return languageSkills;
    }

    public Publication getPublication() {
        return publication;
    }

    public static class Builder {

        private Set<String> changedSections = new HashSet<>();
        private AuditUser auditUser;
        private String fingerprint;
        private String x28OccupationCodes;
        private String externalReference;
        private String externalUrl;
        private boolean reportingObligation;
        private LocalDate reportingObligationEndDate;
        private Employment employment;
        private String jobCenterCode;
        private ApplyChannel applyChannel;
        private Company company;
        private Contact contact;
        private Location location;
        private List<Occupation> occupations;
        private List<LanguageSkill> languageSkills;
        private Publication publication;

        public Builder(AuditUser auditUser) {
            this.auditUser = auditUser;
        }

        public JobAdvertisementUpdater build() {
            return new JobAdvertisementUpdater(this);
        }

        public Builder setFingerprint(String fingerprint) {
            this.changedSections.add(SECTION_FINGERPRINT);
            this.fingerprint = fingerprint;
            return this;
        }

        public Builder setX28OccupationCodes(String x28OccupationCodes) {
            this.changedSections.add(SECTION_X28_OCCUPATION_CODES);
            this.x28OccupationCodes = x28OccupationCodes;
            return this;
        }

        public Builder setExternalReference(String externalReference) {
            this.changedSections.add(SECTION_SOURCE_ENTRY_ID);
            this.externalReference = externalReference;
            return this;
        }

        public Builder setExternalUrl(String externalUrl) {
            this.changedSections.add(SECTION_EXTERNAL_URL);
            this.externalUrl = externalUrl;
            return this;
        }

        public Builder setReportingObligation(boolean reportingObligation, LocalDate reportingObligationEndDate) {
            this.changedSections.add(SECTION_REPORTING_OBLIGATION);
            this.reportingObligation = reportingObligation;
            this.reportingObligationEndDate = reportingObligationEndDate;
            return this;
        }

        public Builder setEmployment(Employment employment) {
            this.changedSections.add(SECTION_EMPLOYMENT);
            this.employment = employment;
            return this;
        }

        public Builder setJobCenterCode(String jobCenterCode) {
            this.changedSections.add(SECTION_JOB_CENTER_CODE);
            this.jobCenterCode = jobCenterCode;
            return this;
        }

        public Builder setApplyChannel(ApplyChannel applyChannel) {
            this.changedSections.add(SECTION_APPLY_CHANNEL);
            this.applyChannel = applyChannel;
            return this;
        }

        public Builder setCompany(Company company) {
            this.changedSections.add(SECTION_COMPANY);
            this.company = company;
            return this;
        }

        public Builder setContact(Contact contact) {
            this.changedSections.add(SECTION_CONTACT);
            this.contact = contact;
            return this;
        }

        public Builder setLocation(Location location) {
            this.changedSections.add(SECTION_LOCATION);
            this.location = location;
            return this;
        }

        public Builder setOccupations(List<Occupation> occupations) {
            this.changedSections.add(SECTION_OCCUPATIONS);
            this.occupations = occupations;
            return this;
        }

        public Builder setLanguageSkills(List<LanguageSkill> languageSkills) {
            this.changedSections.add(SECTION_LANGUAGE_SKILLS);
            this.languageSkills = languageSkills;
            return this;
        }

        public Builder setPublication(Publication publication) {
            this.changedSections.add(SECTION_PUBLICATION);
            this.publication = publication;
            return this;
        }
    }

}
