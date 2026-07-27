package unl.edu.cc.rest.jbrew.filter;

import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import unl.edu.cc.rest.jbrew.bean.SesionUsuario;

import java.io.IOException;

@WebFilter("*.xhtml")
public class AutenticacionFilter implements Filter {

    private static final String PAGINA_LOGIN = "/login.xhtml";

    @Inject
    private SesionUsuario sesionUsuario;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        boolean esRecursoPublico = uri.endsWith(PAGINA_LOGIN)
                || uri.contains("/javax.faces.resource/")
                || uri.contains("/jakarta.faces.resource/");

        if (esRecursoPublico || sesionUsuario.isAutenticado()) {
            chain.doFilter(req, res);
            return;
        }

        response.sendRedirect(request.getContextPath() + PAGINA_LOGIN);
    }
}