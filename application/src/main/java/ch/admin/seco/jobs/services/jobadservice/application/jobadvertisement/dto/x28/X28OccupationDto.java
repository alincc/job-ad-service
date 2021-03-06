package ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.x28;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import ch.admin.seco.jobs.services.jobadservice.application.jobadvertisement.dto.OccupationDto;
import ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement.WorkExperience;

public class X28OccupationDto {

    @NotBlank
    @Size(max = 16)
    private String avamOccupationCode;

    private WorkExperience workExperience;

    @Size(max = 8)
    private String educationCode;

    protected X28OccupationDto() {
        // For reflection libs
    }

    public X28OccupationDto(String avamOccupationCode, WorkExperience workExperience, String educationCode) {
        this.avamOccupationCode = avamOccupationCode;
        this.workExperience = workExperience;
        this.educationCode = educationCode;
    }

    OccupationDto toOccupationDto() {
        return new OccupationDto()
                .setAvamOccupationCode(avamOccupationCode)
                .setWorkExperience(workExperience)
                .setEducationCode(educationCode);
    }

    public String getAvamOccupationCode() {
        return avamOccupationCode;
    }

    public WorkExperience getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(WorkExperience workExperience) {
        this.workExperience = workExperience;
    }

    public void setAvamOccupationCode(String avamOccupationCode) {
        this.avamOccupationCode = avamOccupationCode;
    }

    public String getEducationCode() {
        return educationCode;
    }

    public void setEducationCode(String educationCode) {
        this.educationCode = educationCode;
    }
}
