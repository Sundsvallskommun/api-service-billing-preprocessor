package se.sundsvall.billingpreprocessor.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Objects;

@Entity
@Table(name = "file_configuration",
	indexes = @Index(name = "idx_file_configuration_type_category_tag", columnList = "type, category_tag"),
	uniqueConstraints = @UniqueConstraint(name = "uq_type_category_tag", columnNames = { "type", "category_tag" }))
public class InvoiceFileConfigurationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "type")
	private String type;

	@Column(name = "category_tag")
	private String categoryTag;

	@Column(name = "file_name_pattern")
	private String fileNamePattern;

	public static InvoiceFileConfigurationEntity create() {
		return new InvoiceFileConfigurationEntity();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public InvoiceFileConfigurationEntity withType(String type) {
		this.type = type;
		return this;
	}

	public String getCategoryTag() {
		return categoryTag;
	}

	public void setCategoryTag(String categoryTag) {
		this.categoryTag = categoryTag;
	}

	public InvoiceFileConfigurationEntity withCategoryTag(String categoryTag) {
		this.categoryTag = categoryTag;
		return this;
	}

	public String getFileNamePattern() {
		return fileNamePattern;
	}

	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

	public InvoiceFileConfigurationEntity withFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		InvoiceFileConfigurationEntity that = (InvoiceFileConfigurationEntity) o;
		return id == that.id && Objects.equals(type, that.type) && Objects.equals(categoryTag, that.categoryTag) && Objects.equals(fileNamePattern, that.fileNamePattern);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, type, categoryTag, fileNamePattern);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("InvoiceFileConfigurationEntity{");
		sb.append("id=").append(id);
		sb.append(", type='").append(type).append('\'');
		sb.append(", categoryTag='").append(categoryTag).append('\'');
		sb.append(", fileNamePattern='").append(fileNamePattern).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
