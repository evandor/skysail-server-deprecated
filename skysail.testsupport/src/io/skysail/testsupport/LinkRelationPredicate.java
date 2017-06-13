//package io.skysail.testsupport;
//
//import org.restlet.data.Header;
//import org.restlet.util.Series;
//
//import io.skysail.core.model.LinkModel;
//import io.skysail.core.model.LinkRelation;
//import lombok.ToString;
//
//@ToString(callSuper = true)
//public class LinkRelationPredicate extends LinkPredicate {
//
//    private LinkRelation linkRelation;
//
//    public LinkRelationPredicate(LinkRelation linkRelation, Series<Header> series) {
//        super(series);
//        this.linkRelation = linkRelation;
//
//    }
//
//    @Override
//    public boolean test(LinkModel l) {
//        return l.getRelation().equals(linkRelation);
//    }
//
//}
