package br.com.smartcondo.domain.repository;

import br.com.smartcondo.domain.model.AreaComum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaComumRepository extends JpaRepository<AreaComum, Long> {

    List<AreaComum> findByAtivaTrue();
}
