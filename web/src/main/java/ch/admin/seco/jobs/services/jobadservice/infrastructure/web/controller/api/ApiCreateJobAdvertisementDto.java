package ch.admin.seco.jobs.services.jobadservice.infrastructure.web.controller.api;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class ApiCreateJobAdvertisementDto {

    private boolean reportToAvam;

    @Size(max = 1024)
    private String externalUrl;

    @Size(max = 255)
    private String externalReference;

    @Valid
    @NotNull
    private ApiContactDto contact;

    @Valid
    @NotNull
    private ApiPublicationDto publication;

    @Size(max = 3)
    @Pattern(regexp = "[0-9]*")
    private String numberOfJobs;

    @Valid
    @NotEmpty
    private List<ApiJobDescriptionDto> jobDescriptions;

    @Valid
    @NotNull
    private ApiCompanyDto company;

    @Valid
    private ApiEmployerDto employer;

    @Valid
    @NotNull
    private ApiEmploymentDto employment;

    @Valid
    @NotNull
    private ApiCreateLocationDto location;

    @Valid
    @NotNull
    private ApiOccupationDto occupation;

    @Valid
    private List<ApiLanguageSkillDto> languageSkills;

    @Valid
    @NotNull
    private ApiApplyChannelDto applyChannel;

    @Valid
    private ApiPublicContactDto publicContact;

    protected ApiCreateJobAdvertisementDto() {
        // For reflection libs
    }

    public ApiCreateJobAdvertisementDto(boolean reportToAvam, String externalUrl, String externalReference, ApiContactDto contact, ApiPublicationDto publication, String numberOfJobs, List<ApiJobDescriptionDto> jobDescriptions, ApiCompanyDto company, ApiEmployerDto employer, ApiEmploymentDto employment, ApiCreateLocationDto location, ApiOccupationDto occupation, List<ApiLanguageSkillDto> languageSkills, ApiApplyChannelDto applyChannel, ApiPublicContactDto publicContact) {
        this.reportToAvam = reportToAvam;
        this.externalUrl = externalUrl;
        this.externalReference = externalReference;
        this.contact = contact;
        this.publication = publication;
        this.numberOfJobs = numberOfJobs;
        this.jobDescriptions = jobDescriptions;
        this.company = company;
        this.employer = employer;
        this.employment = employment;
        this.location = location;
        this.occupation = occupation;
        this.languageSkills = languageSkills;
        this.applyChannel = applyChannel;
        this.publicContact = publicContact;
    }

    public boolean isReportToAvam() {
        return reportToAvam;
    }

    public void setReportToAvam(boolean reportToAvam) {
        this.reportToAvam = reportToAvam;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public ApiContactDto getContact() {
        return contact;
    }

    public void setContact(ApiContactDto contact) {
        this.contact = contact;
    }

    public ApiPublicationDto getPublication() {
        return publication;
    }

    public void setPublication(ApiPublicationDto publication) {
        this.publication = publication;
    }

    public String getNumberOfJobs() {
        return numberOfJobs;
    }

    public void setNumberOfJobs(String numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
    }

    public List<ApiJobDescriptionDto> getJobDescriptions() {
        return jobDescriptions;
    }

    public void setJobDescriptions(List<ApiJobDescriptionDto> jobDescriptions) {
        this.jobDescriptions = jobDescriptions;
    }

    public ApiCompanyDto getCompany() {
        return company;
    }

    public void setCompany(ApiCompanyDto company) {
        this.company = company;
    }

    public ApiEmployerDto getEmployer() {
        return employer;
    }

    public void setEmployer(ApiEmployerDto employer) {
        this.employer = employer;
    }

    public ApiEmploymentDto getEmployment() {
        return employment;
    }

    public void setEmployment(ApiEmploymentDto employment) {
        this.employment = employment;
    }

    public ApiCreateLocationDto getLocation() {
        return location;
    }

    public void setLocation(ApiCreateLocationDto location) {
        this.location = location;
    }

    public ApiOccupationDto getOccupation() {
        return occupation;
    }

    public void setOccupation(ApiOccupationDto occupation) {
        this.occupation = occupation;
    }

    public List<ApiLanguageSkillDto> getLanguageSkills() {
        return languageSkills;
    }

    public void setLanguageSkills(List<ApiLanguageSkillDto> languageSkills) {
        this.languageSkills = languageSkills;
    }

    public ApiApplyChannelDto getApplyChannel() {
        return applyChannel;
    }

    public void setApplyChannel(ApiApplyChannelDto applyChannel) {
        this.applyChannel = applyChannel;
    }

    public ApiPublicContactDto getPublicContact() {
        return publicContact;
    }

    public void setPublicContact(ApiPublicContactDto publicContact) {
        this.publicContact = publicContact;
    }
}
