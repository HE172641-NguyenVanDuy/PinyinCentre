package com.pinyincentre.pinyin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Setter
@Getter
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultInfo<T> implements Serializable {
    public static final Long UNAUTHENTICATED = 401L;
    public static final Long RESULT_OK = 200L;
    public static final Long RESULT_NOK = 400L;
    public static final Long RESULT_NOK_403 = 403L;
    public static final Long RESULT_NOK_405 = 405L;
    public static final Long RESULT_NOK_PRECONDITION_FAILED = 412L;
    public static final Long RESULT_NOK_PRECONDITION_REQUIRED = 428L;
    public static final Long RESULT_NOK_CONFLICT = 409L;
    public static final Long RESULT_NOK_SERVICE_UNAVAILABLE = 503L;
    public static final String RESULT_NOK1 = "NOK1";
    public static final String RESULT_NOK2 = "NOK2";
    public static final String RESULT_NOK3 = "NOK3";
    private Long status;
    private String message;
    private Long code;
    private T data;
    private Long ordersId;
    private String imageUrl;
    private T body;
    private Map<String, Object> resultInfo;

    public ResultInfo(Long status, T data) {
        this.status = status;
        this.data = data;
    }

    public ResultInfo(Long status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResultInfo(String message) {
        this.message = message;
    }

    public static <T> ResultInfoBuilder<T> builder() {
        return new ResultInfoBuilder<T>();
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ResultInfo)) {
            return false;
        } else {
            ResultInfo<?> other = (ResultInfo)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$status = this.getStatus();
                Object other$status = other.getStatus();
                if (this$status == null) {
                    if (other$status != null) {
                        return false;
                    }
                } else if (!this$status.equals(other$status)) {
                    return false;
                }

                Object this$code = this.getCode();
                Object other$code = other.getCode();
                if (this$code == null) {
                    if (other$code != null) {
                        return false;
                    }
                } else if (!this$code.equals(other$code)) {
                    return false;
                }

                Object this$ordersId = this.getOrdersId();
                Object other$ordersId = other.getOrdersId();
                if (this$ordersId == null) {
                    if (other$ordersId != null) {
                        return false;
                    }
                } else if (!this$ordersId.equals(other$ordersId)) {
                    return false;
                }

                Object this$message = this.getMessage();
                Object other$message = other.getMessage();
                if (this$message == null) {
                    if (other$message != null) {
                        return false;
                    }
                } else if (!this$message.equals(other$message)) {
                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    if (other$data != null) {
                        return false;
                    }
                } else if (!this$data.equals(other$data)) {
                    return false;
                }

                Object this$imageUrl = this.getImageUrl();
                Object other$imageUrl = other.getImageUrl();
                if (this$imageUrl == null) {
                    if (other$imageUrl != null) {
                        return false;
                    }
                } else if (!this$imageUrl.equals(other$imageUrl)) {
                    return false;
                }

                Object this$body = this.getBody();
                Object other$body = other.getBody();
                if (this$body == null) {
                    if (other$body != null) {
                        return false;
                    }
                } else if (!this$body.equals(other$body)) {
                    return false;
                }

                Object this$resultInfo = this.getResultInfo();
                Object other$resultInfo = other.getResultInfo();
                if (this$resultInfo == null) {
                    if (other$resultInfo != null) {
                        return false;
                    }
                } else if (!this$resultInfo.equals(other$resultInfo)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ResultInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        Object $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        Object $ordersId = this.getOrdersId();
        result = result * 59 + ($ordersId == null ? 43 : $ordersId.hashCode());
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        Object $imageUrl = this.getImageUrl();
        result = result * 59 + ($imageUrl == null ? 43 : $imageUrl.hashCode());
        Object $body = this.getBody();
        result = result * 59 + ($body == null ? 43 : $body.hashCode());
        Object $resultInfo = this.getResultInfo();
        result = result * 59 + ($resultInfo == null ? 43 : $resultInfo.hashCode());
        return result;
    }

    public String toString() {
        Long var10000 = this.getStatus();
        return "ResultInfo(status=" + var10000 + ", message=" + this.getMessage() + ", code=" + this.getCode() + ", data=" + this.getData() + ", ordersId=" + this.getOrdersId() + ", imageUrl=" + this.getImageUrl() + ", body=" + this.getBody() + ", resultInfo=" + this.getResultInfo() + ")";
    }

    public ResultInfo(final Long status, final String message, final Long code, final T data, final Long ordersId, final String imageUrl, final T body, final Map<String, Object> resultInfo) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.data = data;
        this.ordersId = ordersId;
        this.imageUrl = imageUrl;
        this.body = body;
        this.resultInfo = resultInfo;
    }

    public ResultInfo() {
    }

    public static class ResultInfoBuilder<T> {
        private Long status;
        private String message;
        private Long code;
        private T data;
        private Long ordersId;
        private String imageUrl;
        private T body;
        private Map<String, Object> resultInfo;

        ResultInfoBuilder() {
        }

        public ResultInfoBuilder<T> status(final Long status) {
            this.status = status;
            return this;
        }

        public ResultInfoBuilder<T> message(final String message) {
            this.message = message;
            return this;
        }

        public ResultInfoBuilder<T> code(final Long code) {
            this.code = code;
            return this;
        }

        public ResultInfoBuilder<T> data(final T data) {
            this.data = data;
            return this;
        }

        public ResultInfoBuilder<T> ordersId(final Long ordersId) {
            this.ordersId = ordersId;
            return this;
        }

        public ResultInfoBuilder<T> imageUrl(final String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public ResultInfoBuilder<T> body(final T body) {
            this.body = body;
            return this;
        }

        public ResultInfoBuilder<T> resultInfo(final Map<String, Object> resultInfo) {
            this.resultInfo = resultInfo;
            return this;
        }

        public ResultInfo<T> build() {
            return new ResultInfo<T>(this.status, this.message, this.code, this.data, this.ordersId, this.imageUrl, this.body, this.resultInfo);
        }

        public String toString() {
            return "ResultInfo.ResultInfoBuilder(status=" + this.status + ", message=" + this.message + ", code=" + this.code + ", data=" + this.data + ", ordersId=" + this.ordersId + ", imageUrl=" + this.imageUrl + ", body=" + this.body + ", resultInfo=" + this.resultInfo + ")";
        }
    }
}

