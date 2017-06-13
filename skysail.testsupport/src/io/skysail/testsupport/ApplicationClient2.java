//package io.skysail.testsupport;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.restlet.Response;
//import org.restlet.data.ChallengeResponse;
//import org.restlet.data.Header;
//import org.restlet.data.MediaType;
//import org.restlet.data.Method;
//import org.restlet.data.Reference;
//import org.restlet.engine.util.StringUtils;
//import org.restlet.representation.Representation;
//import org.restlet.resource.ClientResource;
//import org.restlet.util.Series;
//
//import io.skysail.core.model.LinkModel;
//import io.skysail.core.model.LinkRelation;
//import io.skysail.testsupport.authentication.AuthenticationStrategy2;
//import lombok.Getter;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class ApplicationClient2 {
//
//    public static final String TESTTAG = " > TEST: ";
//
//    @Getter
//    private String baseUrl;
//    private String credentials;
//    private String url;
//    private ClientResource cr;
//    @Getter
//    private Representation currentRepresentation;
//    private MediaType mediaType = MediaType.TEXT_HTML;
//    private String appName;
//
//    @Getter
//    private Series<Header> currentHeader;
//
//	private ChallengeResponse challengeResponse;
//
//    public ApplicationClient2(@NonNull String baseUrl, @NonNull String appName, @NonNull MediaType mediaType) {
//        this.baseUrl = baseUrl;
//        this.appName = appName;
//        this.mediaType = mediaType;
//    }
//
//    public ApplicationClient2 setUrl(String url) {
//        log.info("{}setting browser client url to '{}'", TESTTAG, url);
//        this.url = url;
//        return this;
//    }
//
//    public Representation get() {
//        String currentUrl = baseUrl + url;
//        log.info("{}issuing GET on '{}', providing credentials {}", TESTTAG, currentUrl, credentials);
//        cr = new ClientResource(currentUrl);
//        //cr.setFollowingRedirects(false);
//        if (!StringUtils.isNullOrEmpty(credentials)) {
//        	cr.getCookies().add("Credentials", credentials);
//        }
//        cr.setChallengeResponse(challengeResponse);
//        return cr.get(mediaType);
//    }
//
//    public ApplicationClient2 gotoRoot() {
//        url = "/";
//        get();
//        return this;
//    }
//
//    public ApplicationClient2 gotoAppRoot() {
//        gotoRoot().followLinkTitle(appName);
//        return this;
//    }
//
//    public ApplicationClient2 gotoUrl(String relUrl) {
//        url = relUrl;
//        currentRepresentation = get();
//        return this;
//    }
//
//
//    public Representation post(Object entity) {
//        log.info("{}issuing POST on '{}', providing credentials {}", TESTTAG, url, credentials);
//        url = (url.contains("?") ? url + "&" : url + "?") + "xxx";//SkysailServerResource.NO_REDIRECTS ;
//        cr = new ClientResource(url);
//        cr.setFollowingRedirects(false);
//        cr.getCookies().add("Credentials", credentials);
//        cr.setChallengeResponse(challengeResponse);
//        return cr.post(entity, mediaType);
//    }
//
//    public Response getResponse() {
//        return cr.getResponse();
//    }
//
//    public ApplicationClient2 loginAs(AuthenticationStrategy2 authenticationStrategy, @NonNull String username, @NonNull String password) {
//        cr = authenticationStrategy.login(this, username, password);
//        challengeResponse = cr.getChallengeResponse();
//        credentials = cr.getCookies().getFirstValue("Credentials");
//        return this;
//    }
//
//    public ApplicationClient2 followLinkTitle(String linkTitle) {
//        return follow(new LinkTitlePredicate(linkTitle, cr.getResponse().getHeaders()));
//    }
//
//    public ApplicationClient2 followLinkTitleAndRefId(String linkTitle, String refId) {
//    	LinkModel example = new LinkModel(refId, "", null, null, null);//new LinkModel.Builder("").title(linkTitle).refId(refId).build();
//        return follow(new LinkByExamplePredicate(example, cr.getResponse().getHeaders()));
//    }
//
//    public ApplicationClient2 followLinkRelation(LinkRelation linkRelation) {
//        return follow(new LinkRelationPredicate(linkRelation, cr.getResponse().getHeaders()));
//    }
//
//    public ApplicationClient2 followLink(Method method) {
//        return followLink(method, null);
//    }
//
//    public ApplicationClient2 followLink(Method method, String entity) {
//        return follow(new LinkMethodPredicate(method, cr.getResponse().getHeaders()), method, entity);
//    }
//
//    private ApplicationClient2 follow(LinkPredicate predicate, Method method, String entity) {
//        currentHeader = cr.getResponse().getHeaders();
//        String linkheader = currentHeader.getFirstValue("Link");
//        if (linkheader == null) {
//            throw new IllegalStateException("no link header found");
//        }
//        List<LinkModel> links = Arrays.stream(linkheader.split(",")).map(l -> LinkModel.fromLinkheader(l)).collect(Collectors.toList());
//        LinkModel theLink = getTheOnlyLink(predicate, links);
//
//        boolean isAbsolute = false;
//        try {
//            URI url2 = new URI(theLink.getUri());
//            isAbsolute = url2.isAbsolute();
//        } catch (URISyntaxException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        url =  isAbsolute ? theLink.getUri() : baseUrl + theLink.getUri();
//        cr = new ClientResource(url);
//        cr.getCookies().add("Credentials", credentials);
//        cr.setChallengeResponse(challengeResponse);
//
//        if (method != null) {
//        	// TODO
////            if (!(theLink.getVerbs().contains(method))) {
////                throw new IllegalStateException("method " + method + " not eligible for link " + theLink);
////            }
//            if (Method.DELETE.equals(method)) {
//                log.info("{}issuing DELETE on '{}', providing credentials {}", TESTTAG, url, credentials);
//                currentRepresentation = cr.delete(mediaType);
//            } else if (Method.POST.equals(method)) {
//                log.info("{}issuing POST on '{}' with entity '{}', providing credentials {}", TESTTAG, url, entity, credentials);
//                currentRepresentation = cr.post(entity, mediaType);
//            } else if (Method.PUT.equals(method)) {
//                log.info("{}issuing PUT on '{}' with entity '{}', providing credentials {}", TESTTAG, url, entity, credentials);
//                currentRepresentation = cr.put(entity, mediaType);
//            } else {
//                throw new UnsupportedOperationException();
//            }
//        } else {
//            currentRepresentation = cr.get(mediaType);
//            //url = currentRepresentation.getLocationRef().toUri().toString();
//        }
//        return this;
//    }
//
//    private ApplicationClient2 follow(LinkPredicate predicate) {
//        return follow(predicate, null, null);
//    }
//
//    private LinkModel getTheOnlyLink(LinkPredicate predicate, List<LinkModel> links) {
//        List<LinkModel> filteredLinks = links.stream().filter(predicate).collect(Collectors.toList());
//        if (filteredLinks.size() == 0 && predicate instanceof LinkTitlePredicate && !(predicate instanceof LinkSubTitlePredicate)) {
//            log.info("didn't find exact link, trying substring");
//            return getTheOnlyLink(new LinkSubTitlePredicate((LinkTitlePredicate)predicate), links);
//        }
//        if (filteredLinks.size() == 0) {
//            throw new IllegalStateException("could not find link for predicate " + predicate);
//        }
//        if (filteredLinks.size() > 1) {
//            throw new IllegalStateException("too many candidates found for predicate " + predicate);
//        }
//        LinkModel theLink = filteredLinks.get(0);
//        return theLink;
//    }
//
//    public Reference getLocation() {
//        return cr.getLocationRef();
//    }
//
//    public void setUrlFromCurrentRepresentation() {
//        url = currentRepresentation.getLocationRef().toUri().toString();
//    }
//
//
//
//}
