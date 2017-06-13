//package io.skysail.testsupport;
//
//import org.restlet.data.Header;
//import org.restlet.util.Series;
//
//import io.skysail.core.model.LinkModel;
//
//public class LinkByExamplePredicate extends LinkPredicate {
//
//    private LinkModel exampleLink;
//
//    public LinkByExamplePredicate(LinkModel exampleLink, Series<Header> series) {
//        super(series);
//        this.exampleLink = exampleLink;
//    }
//
//    @Override
//    public boolean test(LinkModel l) {
//        String title = exampleLink.getTitle();
//        if (title != null && (!(l.getTitle().equals(title)))) {
//            return false;
//        }
//        String refId = exampleLink.getRefId();
//        if (refId != null && (!(l.getRefId().equals(refId)))) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        String superToString = super.toString();
//        StringBuilder sb = new StringBuilder("'LinkByExample' matching: ");
//        sb.append("title = '").append(exampleLink.getTitle()).append("', ");
//        sb.append("refId = '").append(exampleLink.getRefId()).append("'\n in list of links:");
//        sb.append(superToString);
//        return sb.toString();
//    }
//
//}
