package se.sundsvall.billingpreprocessor.integration.db.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "file_configuration",
	indexes = {
		@Index(name = "idx_file_configuration_type_category_tag", columnList = "type, category_tag"),
		@Index(name = "idx_file_configuration_creator_name", columnList = "creator_name")
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_type_category_tag", columnNames = { "type", "category_tag" }),
		@UniqueConstraint(name = "uq_creator_name", columnNames = { "creator_name" })
	})
public class InvoiceFileConfigurationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "type")
	private String type;

	@Column(name = "category_tag")
	private String categoryTag;

	@Column(name = "creator_name")
	private String creatorName;

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

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public InvoiceFileConfigurationEntity withCreatorName(String creatorName) {
		this.creatorName = creatorName;
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
	public int hashCode() {
		return Objects.hash(categoryTag, creatorName, fileNamePattern, id, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof InvoiceFileConfigurationEntity)) {
			return false;
		}
		InvoiceFileConfigurationEntity other = (InvoiceFileConfigurationEntity) obj;
		return Objects.equals(categoryTag, other.categoryTag) && Objects.equals(creatorName, other.creatorName) && Objects.equals(fileNamePattern, other.fileNamePattern) && id == other.id && Objects.equals(type, other.type);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceFileConfigurationEntity [id=").append(id).append(", type=").append(type).append(", categoryTag=").append(categoryTag).append(", creatorName=").append(creatorName).append(", fileNamePattern=").append(fileNamePattern)
			.append("]");
		return builder.toString();
	}
}
