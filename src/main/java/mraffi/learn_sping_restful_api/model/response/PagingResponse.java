package mraffi.learn_sping_restful_api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingResponse {

   private Integer currentPage;
   private Integer totalPage;
   private Integer size;

}