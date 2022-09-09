package br.com.giulianabezerra.sbconditionalflows;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

  @Autowired
  private JobBuilderFactory jobBuilderFactory;

  @Autowired
  private StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job job() {
    return jobBuilderFactory
        .get("job")
        .start(checarTipoDeArquivoClientes())
        .on("CADASTRO").to(cadastrarClientes())
        .from(checarTipoDeArquivoClientes()).on("ATUALIZACAO").to(atualizarClientes()).end()
        .build();
  }

  @Bean
  public Step checarTipoDeArquivoClientes() {
    return stepBuilderFactory
        .get("checarTipoDeArquivoClientes")
        .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
          System.out.println("Checando o tipo de arquivo de clientes...");

          String tipoArquivoClientes = "CADASTRO";
          // String tipoArquivoClientes = "ATUALIZACAO";

          chunkContext.getStepContext().getStepExecution().getExecutionContext()
              .put("TIPO", tipoArquivoClientes);

          return RepeatStatus.FINISHED;
        })
        .listener(new OperationListener())
        .build();
  }

  public Step cadastrarClientes() {
    return stepBuilderFactory
        .get("cadastrarClientes")
        .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
          System.out.println("Cadastrando clientes...");
          return RepeatStatus.FINISHED;
        })
        .build();
  }

  public Step atualizarClientes() {
    return stepBuilderFactory
        .get("atualizarClientes")
        .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
          System.out.println("Atualizando clientes...");
          return RepeatStatus.FINISHED;
        })
        .build();
  }

  class OperationListener {
    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
      if (stepExecution.getExecutionContext().get("TIPO").equals("CADASTRO"))
        return new ExitStatus("CADASTRO");
      else if (stepExecution.getExecutionContext().get("TIPO").equals("ATUALIZACAO"))
        return new ExitStatus("ATUALIZACAO");
      else
        return ExitStatus.FAILED;
    }
  }

}
