package ch.admin.seco.jobs.services.jobadservice.domain.jobadvertisement;

import ch.admin.seco.jobs.services.jobadservice.core.domain.ValueObject;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.util.Objects;


@Embeddable
@Access(AccessType.FIELD)
public class Employer implements ValueObject<Employer> {
    private String name;
    private String postalCode;
    private String city;
    private String countryIsoCode;

    protected Employer() {
        // For reflection libs
    }

    public Employer(Builder builder) {
        this.name = builder.name;
        this.postalCode = builder.postalCode;
        this.city = builder.city;
        this.countryIsoCode = builder.countryIsoCode;
    }

    public String getName() {
        return name;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    @Override
    public String toString() {
        return "Employer{" +
                "name='" + name + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                ", countryIsoCode='" + countryIsoCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employer employer = (Employer) o;
        return Objects.equals(name, employer.name) &&
                Objects.equals(postalCode, employer.postalCode) &&
                Objects.equals(city, employer.city) &&
                Objects.equals(countryIsoCode, employer.countryIsoCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, postalCode, city, countryIsoCode);
    }

    public static final class Builder {
        private String name;
        private String postalCode;
        private String city;
        private String countryIsoCode;

        public Builder() {
        }

        public Employer build() {
            return new Employer(this);
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder setCountryIsoCode(String countryIsoCode) {
            this.countryIsoCode = countryIsoCode;
            return this;
        }
    }
}
