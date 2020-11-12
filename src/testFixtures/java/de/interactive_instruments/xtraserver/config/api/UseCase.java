package de.interactive_instruments.xtraserver.config.api;

import java.util.Objects;

public interface UseCase {

  String title();

  String description();

  String givenDescription();

  String expectedDescription();

  XtraServerMapping given();

  XtraServerMapping expected();

  boolean isVirtualTables();

  static Builder builder() {
    return new Builder();
  }

  class Builder {
    private String title;
    private String description;
    private String givenDescription;
    private String expectedDescription;
    private XtraServerMapping given;
    private XtraServerMapping expected;
    private boolean isVirtualTables;

    public Builder() {
      this.title = "";
      this.description = "";
      this.givenDescription = "";
      this.expectedDescription = "";
      this.isVirtualTables = false;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    Builder given(XtraServerMapping given) {
      this.given = given;
      return this;
    }

    public Builder given(String description, XtraServerMapping given) {
      this.givenDescription = description;
      this.given = given;
      return this;
    }

    Builder expected(XtraServerMapping expected) {
      this.expected = expected;
      return this;
    }

    public Builder expected(String description, XtraServerMapping expected) {
      this.expectedDescription = description;
      this.expected = expected;
      return this;
    }

    public Builder virtualTables() {
      this.isVirtualTables = true;
      return this;
    }

    public UseCase build() {
      if (Objects.isNull(given) || Objects.isNull(expected)) {
        throw new IllegalStateException();
      }

      return new UseCase() {
        @Override
        public String title() {
          return title;
        }

        @Override
        public String description() {
          return description;
        }

        @Override
        public String givenDescription() {
          return givenDescription;
        }

        @Override
        public String expectedDescription() {
          return expectedDescription;
        }

        @Override
        public XtraServerMapping given() {
          return given;
        }

        @Override
        public XtraServerMapping expected() {
          return expected;
        }

        @Override
        public boolean isVirtualTables() {
          return isVirtualTables;
        }
      };
    }
  }
}
