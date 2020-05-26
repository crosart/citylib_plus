package com.citylib.citylibwebapp.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * PageJacksonModule replacement class to fix error on JsonProperty "number" incorrectly decoded to a "page" property.
 *
 * Fixed from <a href="https://github.com/rvervaek/spring-cloud-openfeign/commit/5e05790d4a0415bd4e6b9e3342ce92cc0bdf596b#diff-f5cd661470f6eb3e859e27801a744f1b">Github FIX GH237</a>
 */
public class PageJacksonModuleFixGH237 extends Module {
    public PageJacksonModuleFixGH237() {
    }

    public String getModuleName() {
        return "PageJacksonModuleFixGH237";
    }

    public Version version() {
        return new Version(0, 1, 0, "", (String)null, (String)null);
    }

    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(Page.class, PageJacksonModuleFixGH237.PageMixIn.class);
    }

    static class SimplePageImpl<T> implements Page<T> {
        private final Page<T> delegate;

        SimplePageImpl(@JsonProperty("content") List<T> content, @JsonProperty("number") int number, @JsonProperty("size") int size, @JsonProperty("totalElements") long totalElements) {
            this.delegate = new PageImpl(content, PageRequest.of(number, size), totalElements);
        }

        @JsonProperty
        public int getTotalPages() {
            return this.delegate.getTotalPages();
        }

        @JsonProperty
        public long getTotalElements() {
            return this.delegate.getTotalElements();
        }

        @JsonProperty
        public int getNumber() {
            return this.delegate.getNumber();
        }

        @JsonProperty
        public int getSize() {
            return this.delegate.getSize();
        }

        @JsonProperty
        public int getNumberOfElements() {
            return this.delegate.getNumberOfElements();
        }

        @JsonProperty
        public List<T> getContent() {
            return this.delegate.getContent();
        }

        @JsonProperty
        public boolean hasContent() {
            return this.delegate.hasContent();
        }

        @JsonIgnore
        public Sort getSort() {
            return this.delegate.getSort();
        }

        @JsonProperty
        public boolean isFirst() {
            return this.delegate.isFirst();
        }

        @JsonProperty
        public boolean isLast() {
            return this.delegate.isLast();
        }

        @JsonIgnore
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @JsonIgnore
        public boolean hasPrevious() {
            return this.delegate.hasPrevious();
        }

        @JsonIgnore
        public Pageable nextPageable() {
            return this.delegate.nextPageable();
        }

        @JsonIgnore
        public Pageable previousPageable() {
            return this.delegate.previousPageable();
        }

        @JsonIgnore
        public <S> Page<S> map(Function<? super T, ? extends S> converter) {
            return this.delegate.map(converter);
        }

        @JsonIgnore
        public Iterator<T> iterator() {
            return this.delegate.iterator();
        }
    }

    @JsonDeserialize(
        as = PageJacksonModuleFixGH237.SimplePageImpl.class
    )
    private interface PageMixIn {
    }
}
