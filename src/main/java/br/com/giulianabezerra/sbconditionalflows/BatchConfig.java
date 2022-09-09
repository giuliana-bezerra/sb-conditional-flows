package br.com.giulianabezerra.sbconditionalflows;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
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
        .next(cadastrarClientes())
        .next(atualizarClientes())
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

          chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
              .put("TIPO", tipoArquivoClientes);

          return RepeatStatus.FINISHED;
        })
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

}
