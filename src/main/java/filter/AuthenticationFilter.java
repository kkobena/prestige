package filter;

import dal.TUser;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import rest.service.SessionHelperService;
import util.Constant;

/**
 *
 * @author koben
 */
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private HttpServletRequest servletRequest;
    @EJB
    private SessionHelperService sessionHelperService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        HttpSession hs = servletRequest.getSession();
        TUser tu = (TUser) hs.getAttribute(Constant.AIRTIME_USER);
        if (!skipPath(requestContext.getUriInfo().getPath()) && Objects.isNull(tu)) {

            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).entity(Constant.DECONNECTED_MESSAGE).build());

        }

        if (Objects.nonNull(tu)) {
            this.sessionHelperService.setCurrentUser(tu);
        }

    }

    private boolean skipPath(String path) {
        Set<String> paths = Set.of("v1/user/auth", "v1/user/logout");
        return paths.contains(path);
    }
}
