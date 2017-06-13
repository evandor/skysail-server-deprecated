//package io.skysail.testsupport;
//
//import org.restlet.data.Header;
//import org.restlet.util.Series;
//
//import io.skysail.core.model.LinkModel;
//
//
//public class LinkSubTitlePredicate extends LinkPredicate {
//
//    private String title;
//
//    public LinkSubTitlePredicate(String title, Series<Header> series) {
//        super(series);
//        this.title = title;
//    }
//
//    public LinkSubTitlePredicate(LinkTitlePredicate linkTitlePredicate) {
//        super(linkTitlePredicate.series);
//        this.title = linkTitlePredicate.title;
//    }
//
//    @Override
//    public boolean test(LinkModel l) {
//        return l.getTitle().toLowerCase().contains(title.toLowerCase());
//    }
//
//    @Override
//    public String toString() {
//        return new StringBuilder("LinkTitlePredicate(title contains '").append(title).append("', available links: \n")
//                .append(super.toString()).toString();
//    }
//
//}
