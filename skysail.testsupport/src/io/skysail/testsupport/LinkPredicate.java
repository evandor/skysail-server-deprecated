//package io.skysail.testsupport;
//
//import java.util.function.Predicate;
//
//import org.restlet.data.Header;
//import org.restlet.util.Series;
//
//import io.skysail.core.model.LinkModel;
//import lombok.Getter;
//
//@Getter
//public abstract class LinkPredicate implements Predicate<LinkModel> {
//
//    protected String link;
//    protected Series<Header> series;
//
//    public LinkPredicate(Series<Header> series) {
//        this.series = series;
//        this.link = series.getFirstValue("Link");
//    }
//
//    @Override
//    public abstract boolean test(LinkModel l);
//
//    @Override
//    public String toString() {
//        return "\n - " + link.replace(",", "\n - ");
//    }
//}