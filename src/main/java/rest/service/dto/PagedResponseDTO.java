package rest.service.dto;

/**
 *
 * @author airman
 */

import java.io.Serializable;
import java.util.List;

public class PagedResponseDTO<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int total;
    private List<T> data;

    public PagedResponseDTO() {
    }

    public PagedResponseDTO(int total, List<T> data) {
        this.total = total;
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
