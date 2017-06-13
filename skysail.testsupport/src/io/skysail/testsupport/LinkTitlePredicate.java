//package io.skysail.testsupport;
//
//import org.restlet.data.Header;
//import org.restlet.util.Series;
//
//import io.skysail.core.model.LinkModel;
//
//
//public class LinkTitlePredicate extends LinkPredicate {
//
//    protected String title;
//
//    public LinkTitlePredicate(String title, Series<Header> series) {
//        super(series);
//        this.title = title;
//    }
//
//    @Override
//    public boolean test(LinkModel l) {
//        return l.getTitle().equals(title);
//    }
//
//    @Override
//    public String toString() {
//        return new StringBuilder("LinkTitlePredicate(title='").append(title).append("', available links: \n")
//                .append(super.toString()).toString();
//    }
//
//}
