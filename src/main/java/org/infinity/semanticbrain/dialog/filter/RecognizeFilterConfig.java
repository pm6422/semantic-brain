package org.infinity.semanticbrain.dialog.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecognizeFilterConfig {

    private List<RecognizeFilter> filters;

}
