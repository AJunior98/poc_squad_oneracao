package org.br.agro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class USDBRL {

    public String code;
    public String codein;
    public String name;
    public String high;
    public String low;
    public String varBid;
    public String pctChange;
    public String bid;
    public String ask;
    public String timestamp;
    public String create_date;

}
